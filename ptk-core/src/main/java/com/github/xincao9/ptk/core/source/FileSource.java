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
package com.github.xincao9.ptk.core.source;

import com.github.xincao9.ptk.core.Source;
import com.github.xincao9.ptk.core.Logger;
import com.github.xincao9.ptk.core.thread.Worker;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件数据源
 *
 * @author xincao9@gmail.com
 */
public class FileSource implements Source {

    private final String filename;
    private final String div;
    private final int[] indexs;
    private int size = Integer.MAX_VALUE;

    /**
     * 构造器
     * 
     * @param filename 文件名
     * @param div 分隔符
     * @param indexs 索引
     */
    public FileSource(String filename, String div, int[] indexs) {
       this(filename, div, indexs, Integer.MAX_VALUE);
    }

    /**
     * 构造器
     * 
     * @param filename 文件名
     * @param div 分隔符
     * @param indexs 索引
     * @param size 行号
     */
    public FileSource(String filename, String div, int[] indexs, int size) {
        this.filename = filename;
        this.div = div;
        this.indexs = indexs;
        this.size = size;
    }

    /**
     * 读取文件
     * 
     * @return 读取行数
     */
    @Override
    public int read() {
        File file = new File(filename);
        FileInputStream fis;
        InputStreamReader isr;
        BufferedReader br;
        try {
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
        } catch (FileNotFoundException ex) {
            Logger.info(ex.getMessage());
            return 0;
        }
        for (int index : indexs) {
            if (index < 0) {
                Logger.info(String.format("indexs 中存在小于0的索引 %d", index));
                return -1;
            }
        }
        String line;
        int rows = 0;
        try {
            while ((line = br.readLine()) != null) {
                if (rows >= size) {
                    return rows;
                }
                String[] fields = line.split(div);
                if (fields == null) {
                    continue;
                }
                List<String> params = new ArrayList();
                int len = fields.length;
                if (indexs == null) {
                    for (int i = 0; i < len; i++) {
                        if (fields[i] == null) {
                            params.add("");
                        } else {
                            params.add(fields[i]);
                        }
                    }
                } else {
                    for (int index : indexs) {
                        if (index >= len) {
                            continue;
                        }
                        if (fields[index] == null) {
                            params.add("");
                        } else {
                            params.add(fields[index]);
                        }
                    }
                }
                Worker.submit(params);
                rows++;
            }
        } catch (IOException ex) {
            Logger.info(ex.getMessage());
            return -1;
        } finally {
            try {
                br.close();
                isr.close();
                fis.close();
            } catch (IOException ex) {
                Logger.info(ex.getMessage());
            }
        }
        return rows;
    }
}
