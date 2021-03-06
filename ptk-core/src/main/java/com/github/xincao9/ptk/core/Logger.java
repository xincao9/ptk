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
package com.github.xincao9.ptk.core;

import java.util.Date;

/**
 * 记录器
 * 
 * @author xincao9@gmail.com
 */
public class Logger {

    /**
     * 信息
     * 
     * @param message 消息
     */
    public static void info(Object message) {
        System.out.println(String.format("[%s-%s] %s", Thread.currentThread().getName(), new Date().toString(), message));
    }
}
