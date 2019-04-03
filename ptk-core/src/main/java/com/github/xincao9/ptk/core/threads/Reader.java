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
package com.github.xincao9.ptk.core.threads;

import com.github.xincao9.ptk.core.interfaces.Source;
import com.github.xincao9.ptk.core.util.Logger;

/**
 * 单线程读取测试数据
 *
 * @author xincao9@gmail.com
 */
public class Reader implements Runnable {

    private static Source source;

    private Reader() {
    }

    public static void start(Source source) {
        Reader.source = source;
        Logger.info("读线程启动");
        Reader reader = new Reader();
        Thread thread = new Thread(reader, "读线程");
        thread.start();
    }

    @Override
    public void run() {
        Reader.source.read();
        Logger.info("读线程关闭");
    }

}
