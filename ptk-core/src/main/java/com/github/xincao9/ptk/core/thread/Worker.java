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
package com.github.xincao9.ptk.core.thread;

import com.github.xincao9.ptk.core.Method;
import com.github.xincao9.ptk.core.Logger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 工作线程
 *
 * @author xincao9@gmail.com
 */
public class Worker implements Runnable {

    private static final BlockingQueue<Object> TASKS = new LinkedBlockingDeque();
    public static Long cd = 50L;
    public static int concurrent = Runtime.getRuntime().availableProcessors();
    private static Method method;
    private static Thread[] threads;

    /**
     * 构造器
     * 
     */
    private Worker() {
    }

    /**
     * 启动工作线程
     * 
     * @param method 方法
     */
    public static void start(Method method) {
        start(concurrent, cd, method);
    }

    /**
     * 启动工作线程
     * 
     * @param concurrent 并发
     * @param cd 冷却
     * @param method 方法
     */
    public static void start(int concurrent, long cd, Method method) {
        Worker.concurrent = concurrent;
        Worker.cd = cd;
        Worker.method = method;
        Worker.threads = new Thread[Worker.concurrent];
        for (int no = 0; no < Worker.concurrent; no++) {
            Thread thread = new Thread(new Worker(), String.format("%s - %d", "工作线程", no));
            thread.start();
            threads[no] = thread;
        }
    }

    /**
     * 运行方法
     * 
     */
    @Override
    public void run() {
        while (true) {
            try {
                method.run(TASKS.take());
                if (cd > 0) {
                    int time = (int) (Math.random() * cd * 2) + 1; // 模拟请求，期望延迟为 CD
                    Thread.sleep(time);
                }
            } catch (InterruptedException ex) {
                Logger.info("工作线程关闭完成");
                return;
            }
        }
    }

    /**
     * 关闭
     * 
     */
    public static void shutdown() {
        for (Thread t : threads) {
            t.interrupt();
        }
    }

    /**
     * 提交任务
     * 
     * @param task 任务
     */
    public static void submit (Object task) {
        try {
            TASKS.put(task);
        } catch (InterruptedException ie) {
            Logger.info(ie.getMessage());
        }
    }

    /**
     * 等待执行的任务数
     * 
     * @return 任务数
     */
    public static Integer size () {
        return TASKS.size();
    }
}
