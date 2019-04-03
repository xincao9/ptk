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
package com.github.xincao9.ptk.core.util;

import com.github.xincao9.ptk.core.interfaces.Source;
import com.github.xincao9.ptk.core.service.TaskPoolService;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author xincao9@gmail.com
 */
public class FileSource implements Source {

    private String filename;
    private String div;
    private int[] indexs;
    private int size = Integer.MAX_VALUE;
    private static final TaskPoolService taskPool = TaskPoolService.getInstance();

    public FileSource(String filename, String div, int[] indexs) {
        this.filename = filename;
        this.div = div;
        this.indexs = indexs;
    }

    public FileSource(String filename, String div, int[] indexs, int size) {
        this.filename = filename;
        this.div = div;
        this.indexs = indexs;
        this.size = size;
    }

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
                List<String> params = new ArrayList<String>();
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
                taskPool.add(params);
                rows++;
            }
        } catch (IOException ex) {
            Logger.info(ex.getMessage());
            return -1;
        } catch (InterruptedException ex) {
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
