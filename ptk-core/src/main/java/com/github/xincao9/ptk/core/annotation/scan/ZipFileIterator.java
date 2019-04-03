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
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 *
 * @author xincao9@gmail.com
 */
public final class ZipFileIterator {

    private final ZipFile zipFile;
    private final Enumeration<? extends ZipEntry> entries;
    private ZipEntry current;

    ZipFileIterator(File file) throws IOException {
        this.zipFile = new ZipFile(file);
        this.entries = this.zipFile.entries();
    }

    public ZipEntry getEntry() {
        return this.current;
    }

    public InputStream next() throws IOException {
        while (this.entries.hasMoreElements()) {
            this.current = ((ZipEntry) this.entries.nextElement());
            if (!this.current.isDirectory()) {
                return this.zipFile.getInputStream(this.current);
            }
        }
        try {
            this.zipFile.close();
        } catch (IOException ex) {
        }
        return null;
    }
}
