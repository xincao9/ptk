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

import com.github.xincao9.ptk.core.interfaces.Method;
import com.github.xincao9.ptk.core.service.TaskPoolService;
import com.github.xincao9.ptk.core.util.Logger;

/**
 * 工作线程,用来模拟并发用户
 *
 * @author xincao9@gmail.com
 */
public class Worker implements Runnable {

    private static final TaskPoolService taskPool = TaskPoolService.getInstance();
    private static long CD = 50;
    private static int concurrent = 1;
    private static Method METHOD;
    private static Thread[] threads;

    private Worker() {
    }

    public static void start(Method method) {
        METHOD = method;
        threads = new Thread[Worker.concurrent];
        for (int no = 0; no < Worker.concurrent; no++) {
            Thread thread = new Thread(new Worker(), String.format("%s - %d", "工作线程", no));
            thread.start();
        }
    }

    public static void start(int concurrent, long cd, Method method) {
        Worker.concurrent = concurrent;
        Worker.CD = cd;
        METHOD = method;
        threads = new Thread[Worker.concurrent];
        for (int no = 0; no < Worker.concurrent; no++) {
            Thread thread = new Thread(new Worker(), String.format("%s - %d", "工作线程", no));
            thread.start();
            threads[no] = thread;
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                METHOD.run(taskPool.remove());
                if (CD > 0) {
                    int time = (int) (Math.random() * CD) + 1;
                    Thread.sleep(time);
                }
            } catch (InterruptedException ex) {
                Logger.info("工作线程关闭完成");
                return;
            }
        }
    }

    public static void close() {
        for (Thread t : threads) {
            t.interrupt();
        }
    }
}
