/*
 * Copyright 2018 xincao9@gmail.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.xincao9.ptk.core.annotation.scan;

import java.io.DataInput;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author xincao9@gmail.com
 */
public final class AnnotationDetector {

    private final ClassFileBuffer cpBuffer = new ClassFileBuffer();
    private final Map<String, Class<? extends Annotation>> annotations;
    private TypeReporter typeReporter;
    private FieldReporter fieldReporter;
    private MethodReporter methodReporter;
    private String typeName;
    private Object[] constantPool;
    private String memberName;

    public AnnotationDetector(Reporter reporter) {
        Class[] a = reporter.annotations();
        this.annotations = new HashMap(a.length);
        for (int i = 0; i < a.length; i++) {
            this.annotations.put("L" + a[i].getName().replace('.', '/') + ";", a[i]);
        }
        if ((reporter instanceof TypeReporter)) {
            this.typeReporter = ((TypeReporter) reporter);
        }
        if ((reporter instanceof FieldReporter)) {
            this.fieldReporter = ((FieldReporter) reporter);
        }
        if ((reporter instanceof MethodReporter)) {
            this.methodReporter = ((MethodReporter) reporter);
        }
        if ((this.typeReporter == null) && (this.fieldReporter == null) && (this.methodReporter == null)) {
            throw new AssertionError("No reporter defined");
        }
    }

    public final void detect() throws IOException {
        detect(new ClassFileIterator());
    }

    public final void detect(String[] packageNames) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Set files = new HashSet();
        for (String packageName : packageNames) {
            String internalPackageName = packageName.replace('.', '/');
            if (!internalPackageName.endsWith("/")) {
                internalPackageName = internalPackageName.concat("/");
            }
            Enumeration resourceEnum = classLoader.getResources(internalPackageName);
            while (resourceEnum.hasMoreElements()) {
                URL url = (URL) resourceEnum.nextElement();
                boolean isVfs = "vfs".equals(url.getProtocol());
                if (("file".equals(url.getProtocol())) || (isVfs)) {
                    File dir = toFile(url);
                    if (dir.isDirectory()) {
                        files.add(dir);
                    } else if (isVfs) {
                        String jarPath = dir.getPath();
                        int idx = jarPath.indexOf(".jar");
                        if (idx > -1) {
                            jarPath = jarPath.substring(0, idx + 4);
                            File jarFile = new File(jarPath);
                            if (jarFile.isFile()) {
                                files.add(jarFile);
                            }
                        }
                    }
                } else {
                    File jarFile = toFile(((JarURLConnection) url.openConnection()).getJarFileURL());
                    if (jarFile.isFile()) {
                        files.add(jarFile);
                    } else {
                        throw new AssertionError("Not a File: " + jarFile);
                    }
                }
            }
        }
        if (!files.isEmpty()) {
            detect((File[]) files.toArray(new File[files.size()]));
        }
    }

    public final void detect(File[] filesOrDirectories) throws IOException {
        detect(new ClassFileIterator(filesOrDirectories));
    }

    private File toFile(URL url) throws UnsupportedEncodingException {
        return new File(URLDecoder.decode(url.getFile(), "UTF-8"));
    }

    private void detect(ClassFileIterator iterator) throws IOException {
        InputStream stream;
        while ((stream = iterator.next()) != null) {
            try {
                this.cpBuffer.readFrom(stream);
                if (hasCafebabe(this.cpBuffer)) {
                    detect(this.cpBuffer);
                }
            } catch (Throwable t) {
                if (!iterator.isFile()) {
                    stream.close();
                }
            } finally {
                if (iterator.isFile()) {
                    stream.close();
                }
            }
        }
    }

    private boolean hasCafebabe(ClassFileBuffer buffer) throws IOException {
        return (buffer.size() > 4) && (buffer.readInt() == -889275714);
    }

    private void detect(DataInput di) throws IOException {
        readVersion(di);
        readConstantPoolEntries(di);
        readAccessFlags(di);
        readThisClass(di);
        readSuperClass(di);
        readInterfaces(di);
        readFields(di);
        readMethods(di);
        readAttributes(di, 'T', this.typeReporter == null);
    }

    private void readVersion(DataInput di) throws IOException {
        di.skipBytes(4);
    }

    private void readConstantPoolEntries(DataInput di) throws IOException {
        int count = di.readUnsignedShort();
        this.constantPool = new Object[count];
        for (int i = 1; i < count; i++) {
            if (readConstantPoolEntry(di, i)) {
                i++;
            }
        }
    }

    private boolean readConstantPoolEntry(DataInput di, int index) throws IOException {
        int tag = di.readUnsignedByte();
        switch (tag) {
            case 1:
                this.constantPool[index] = di.readUTF();
                return false;
            case 3:
                di.skipBytes(4);
                return false;
            case 4:
                di.skipBytes(4);
                return false;
            case 5:
                di.skipBytes(8);
                return true;
            case 6:
                di.skipBytes(8);
                return true;
            case 7:
            case 8:
                this.constantPool[index] = Integer.valueOf(di.readUnsignedShort());
                return false;
            case 9:
            case 10:
            case 11:
            case 12:
                di.skipBytes(4);
                return false;
            case 2:
        }
        throw new ClassFormatError("Unkown tag value for constant pool entry: " + tag);
    }

    private void readAccessFlags(DataInput di) throws IOException {
        di.skipBytes(2);
    }

    private void readThisClass(DataInput di) throws IOException {
        this.typeName = resolveUtf8(di);
    }

    private void readSuperClass(DataInput di) throws IOException {
        di.skipBytes(2);
    }

    private void readInterfaces(DataInput di) throws IOException {
        int count = di.readUnsignedShort();
        di.skipBytes(count * 2);
    }

    private void readFields(DataInput di) throws IOException {
        int count = di.readUnsignedShort();
        for (int i = 0; i < count; i++) {
            readAccessFlags(di);
            this.memberName = resolveUtf8(di);
            String descriptor = resolveUtf8(di);
            readAttributes(di, 'F', this.fieldReporter == null);
        }
    }

    private void readMethods(DataInput di) throws IOException {
        int count = di.readUnsignedShort();
        for (int i = 0; i < count; i++) {
            readAccessFlags(di);
            this.memberName = resolveUtf8(di);
            String descriptor = resolveUtf8(di);
            readAttributes(di, 'M', this.methodReporter == null);
        }
    }

    private void readAttributes(DataInput di, char reporterType, boolean skipReporting) throws IOException {
        int count = di.readUnsignedShort();
        for (int i = 0; i < count; i++) {
            String name = resolveUtf8(di);
            int length = di.readInt();
            if ((!skipReporting) && (("RuntimeVisibleAnnotations".equals(name)) || ("RuntimeInvisibleAnnotations".equals(name)))) {
                readAnnotations(di, reporterType);
            } else {
                di.skipBytes(length);
            }
        }
    }

    private void readAnnotations(DataInput di, char reporterType) throws IOException {
        int count = di.readUnsignedShort();
        for (int i = 0; i < count; i++) {
            String rawTypeName = readAnnotation(di);
            Class type = (Class) this.annotations.get(rawTypeName);
            if (type != null) {
                String externalTypeName = this.typeName.replace('/', '.');
                switch (reporterType) {
                    case 'T':
                        this.typeReporter.reportTypeAnnotation(type, externalTypeName);
                        break;
                    case 'F':
                        this.fieldReporter.reportFieldAnnotation(type, externalTypeName, this.memberName);
                        break;
                    case 'M':
                        this.methodReporter.reportMethodAnnotation(type, externalTypeName, this.memberName);
                        break;
                    default:
                        throw new AssertionError("reporterType=" + reporterType);
                }
            }
        }
    }

    private String readAnnotation(DataInput di) throws IOException {
        String rawTypeName = resolveUtf8(di);
        int count = di.readUnsignedShort();
        for (int i = 0; i < count; i++) {
            di.skipBytes(2);
            readAnnotationElementValue(di);
        }
        return rawTypeName;
    }

    private void readAnnotationElementValue(DataInput di) throws IOException {
        int tag = di.readUnsignedByte();
        switch (tag) {
            case 66:
            case 67:
            case 68:
            case 70:
            case 73:
            case 74:
            case 83:
            case 90:
            case 115:
                di.skipBytes(2);
                break;
            case 101:
                di.skipBytes(4);
                break;
            case 99:
                di.skipBytes(2);
                break;
            case 64:
                readAnnotation(di);
                break;
            case 91:
                int count = di.readUnsignedShort();
                for (int i = 0; i < count; i++) {
                    readAnnotationElementValue(di);
                }
                break;
            case 65:
            case 69:
            case 71:
            case 72:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 80:
            case 81:
            case 82:
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 92:
            case 93:
            case 94:
            case 95:
            case 96:
            case 97:
            case 98:
            case 100:
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
            case 110:
            case 111:
            case 112:
            case 113:
            case 114:
            default:
                throw new ClassFormatError("Not a valid annotation element type tag: 0x" + Integer.toHexString(tag));
        }
    }

    private String resolveUtf8(DataInput di) throws IOException {
        int index = di.readUnsignedShort();
        Object value = this.constantPool[index];
        String s;
        if ((value instanceof Integer)) {
            s = (String) this.constantPool[((Integer) value).intValue()];
        } else {
            s = (String) value;
        }

        return s;
    }

    public static abstract interface MethodReporter extends AnnotationDetector.Reporter {

        public abstract void reportMethodAnnotation(Class<? extends Annotation> paramClass, String paramString1, String paramString2);
    }

    public static abstract interface FieldReporter extends AnnotationDetector.Reporter {

        public abstract void reportFieldAnnotation(Class<? extends Annotation> paramClass, String paramString1, String paramString2);
    }

    public static abstract interface TypeReporter extends AnnotationDetector.Reporter {

        public abstract void reportTypeAnnotation(Class<? extends Annotation> paramClass, String paramString);
    }

    public static abstract interface Reporter {

        public abstract Class<? extends Annotation>[] annotations();
    }
}
