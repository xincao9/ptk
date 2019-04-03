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
import com.github.xincao9.ptk.core.thread.Worker;

/**
 * 序列化数据源
 * 
 * @author xincao9@gmail.com
 */
public class SequenceSource implements Source {

    private final int max;

    /**
     * 构造器
     * 
     * @param max 最大编号
     */
    public SequenceSource(int max) {
        this.max = max;
    }

    /**
     * 读取序列
     * 
     * @return 序列大小
     */
    @Override
    public int read() {
        for (int i = 1; i <= max; i++) {
            Worker.submit(i);
        }
        return max;
    }
}
