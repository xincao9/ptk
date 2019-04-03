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

/**
 * 压测结果回调
 * 
 * @author xincao9@gmail.com
 */
public interface Result {

    /**
     * 接口输出
     * 
     * @param concurrency 并发数
     * @param total 请求总数
     * @param messageSize 消息大小
     * @param minRT 最小响应时间
     * @param maxRT 最大响应时间
     * @param averageRT 平均响应时间
     * @param tps 每秒处理事务数
     * @param errorNumber 错误率
     */
    public void output(int concurrency, int total, int messageSize, long minRT, long maxRT, double averageRT, double tps, double errorNumber);
}
