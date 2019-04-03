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
package com.github.xincao9.ptk.core.service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 *
 * @author xincao9@gmail.com
 */
public class TaskPoolService {

    private static TaskPoolService taskPool;

    private TaskPoolService() {
    }

    public static TaskPoolService getInstance() {
        if (taskPool == null) {
            taskPool = new TaskPoolService();
        }
        return taskPool;
    }

    private final BlockingQueue<Object> tasks = new LinkedBlockingDeque<Object>();

    public Object remove() throws InterruptedException {
        return tasks.take();
    }

    public void add(Object ps) throws InterruptedException {
        tasks.put(ps);
    }

    public int size() {
        return tasks.size();
    }
}
