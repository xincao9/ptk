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
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author xincao9@gmail.com
 */
final class ClassFileBuffer implements DataInput {

    private byte[] buffer;
    private int size;
    private int pointer;

    ClassFileBuffer() {
        this(8192);
    }

    ClassFileBuffer(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("initialCapacity < 1: " + initialCapacity);
        }
        this.buffer = new byte[initialCapacity];
    }

    public void readFrom(InputStream in) throws IOException {
        this.pointer = 0;
        this.size = 0;
        int n;
        do {
            n = in.read(this.buffer, this.size, this.buffer.length - this.size);
            if (n > 0) {
                this.size += n;
            }
            resizeIfNeeded();
        } while (n >= 0);
    }

    public void seek(int position) throws IOException {
        if (position < 0) {
            throw new IllegalArgumentException("position < 0: " + position);
        }
        if (position > this.size) {
            throw new EOFException();
        }
        this.pointer = position;
    }

    public int size() {
        return this.size;
    }

    @Override
    public void readFully(byte[] bytes) throws IOException {
        readFully(bytes, 0, bytes.length);
    }

    @Override
    public void readFully(byte[] bytes, int offset, int length) throws IOException {
        if ((length < 0) || (offset < 0) || (offset + length > bytes.length)) {
            throw new IndexOutOfBoundsException();
        }
        if (this.pointer + length > this.size) {
            throw new EOFException();
        }
        System.arraycopy(this.buffer, this.pointer, bytes, offset, length);
        this.pointer += length;
    }

    @Override
    public int skipBytes(int n) throws IOException {
        seek(this.pointer + n);
        return n;
    }

    @Override
    public byte readByte() throws IOException {
        if (this.pointer >= this.size) {
            throw new EOFException();
        }
        return this.buffer[(this.pointer++)];
    }

    @Override
    public boolean readBoolean() throws IOException {
        return readByte() != 0;
    }

    @Override
    public int readUnsignedByte() throws IOException {
        if (this.pointer >= this.size) {
            throw new EOFException();
        }
        return read();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        if (this.pointer + 2 > this.size) {
            throw new EOFException();
        }
        return (read() << 8) + read();
    }

    @Override
    public short readShort() throws IOException {
        return (short) readUnsignedShort();
    }

    @Override
    public char readChar() throws IOException {
        return (char) readUnsignedShort();
    }

    @Override
    public int readInt() throws IOException {
        if (this.pointer + 4 > this.size) {
            throw new EOFException();
        }
        return (read() << 24) + (read() << 16) + (read() << 8) + read();
    }

    @Override
    public long readLong() throws IOException {
        if (this.pointer + 8 > this.size) {
            throw new EOFException();
        }
        return (read() << 56) + (read() << 48) + (read() << 40) + (read() << 32) + (read() << 24) + (read() << 16) + (read() << 8) + read();
    }

    @Override
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    @Override
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    @Deprecated
    @Override
    public String readLine() throws IOException {
        throw new UnsupportedOperationException("readLine() is deprecated and not supported");
    }

    @Override
    public String readUTF() throws IOException {
        return DataInputStream.readUTF(this);
    }

    private int read() {
        return this.buffer[(this.pointer++)] & 0xFF;
    }

    private void resizeIfNeeded() {
        if (this.size >= this.buffer.length) {
            byte[] newBuffer = new byte[this.buffer.length * 2];
            System.arraycopy(this.buffer, 0, newBuffer, 0, this.buffer.length);
            this.buffer = newBuffer;
        }
    }
}
