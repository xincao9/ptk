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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author xincao9@gmail.com
 */
public final class ClassFileIterator {

    private final FileIterator fileIterator;
    private ZipFileIterator zipIterator;
    private boolean isFile;

    ClassFileIterator()
            throws IOException {
        this(classPath());
    }

    public ClassFileIterator(File[] filesOrDirectories)
            throws IOException {
        this.fileIterator = new FileIterator(filesOrDirectories);
    }

    public String getName() {
        return this.zipIterator == null ? this.fileIterator.getFile().getPath() : this.zipIterator.getEntry().getName();
    }

    public boolean isFile() {
        return this.isFile;
    }

    public InputStream next()
            throws IOException {
        if (this.zipIterator == null) {
            File file = this.fileIterator.next();
            if (file == null) {
                return null;
            }
            String name = file.getName();
            if (name.endsWith(".class")) {
                this.isFile = true;
                return new FileInputStream(file);
            }
            if ((this.fileIterator.isRootFile()) && (endsWithIgnoreCase(name, ".jar"))) {
                this.zipIterator = new ZipFileIterator(file);
            }
            return next();
        }

        InputStream is = this.zipIterator.next();
        if (is == null) {
            this.zipIterator = null;
            return next();
        }
        this.isFile = false;
        return is;
    }

    private static File[] classPath() {
        String[] fileNames = System.getProperty("java.class.path").split(File.pathSeparator);
        File[] files = new File[fileNames.length];
        for (int i = 0; i < files.length; i++) {
            files[i] = new File(fileNames[i]);
        }
        return files;
    }

    private static boolean endsWithIgnoreCase(String value, String suffix) {
        int n = suffix.length();
        return value.regionMatches(true, value.length() - n, suffix, 0, n);
    }
}
