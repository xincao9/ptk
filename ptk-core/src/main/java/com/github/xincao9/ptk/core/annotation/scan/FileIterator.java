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
import java.util.Deque;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 *
 * @author xincao9@gmail.com
 */
public final class FileIterator {

    private final Deque<File> stack = new LinkedList();
    private int rootCount;
    private File current;

    public FileIterator(File[] filesOrDirectories) {
        addReverse(filesOrDirectories);
        this.rootCount = this.stack.size();
    }

    public File getFile() {
        return this.current;
    }

    public boolean isRootFile() {
        if (this.current == null) {
            throw new NoSuchElementException();
        }
        return this.stack.size() < this.rootCount;
    }

    public File next() throws IOException {
        if (this.stack.isEmpty()) {
            this.current = null;
            return null;
        }
        this.current = ((File) this.stack.removeLast());
        if (this.current.isDirectory()) {
            if (this.stack.size() < this.rootCount) {
                this.rootCount = this.stack.size();
            }
            addReverse(this.current.listFiles());
            return next();
        }
        return this.current;
    }

    private void addReverse(File[] files) {
        for (int i = files.length - 1; i >= 0; i--) {
            this.stack.add(files[i]);
        }
    }
}
