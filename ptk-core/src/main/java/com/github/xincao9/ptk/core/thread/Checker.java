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

import com.github.xincao9.ptk.core.Result;
import com.github.xincao9.ptk.core.Report;
import com.github.xincao9.ptk.core.Logger;

/**
 * 状态检测线程
 * 
 * @author xincao9@gmail.com
 */
public class Checker implements Runnable {

    private static Long cd = 1000L;
    private static final Integer CONFIRM_NUMBER_MAX = 2;
    private final Result result;

    /**
     * 构造器
     * 
     * @param result 结果回调
     */
    private Checker(Result result) {
        this.result = result;
    }

    /**
     * 构造器
     * 
     * @param result 结果回调
     */
    public static void start(Result result) {
        start(cd, result);
    }

    /**
     * 构造器
     * 
     * @param cd 冷却时间
     * @param result 结果回调
     */
    public static void start(long cd, Result result) {
        Checker.cd = cd;
        Logger.info("状态检测线程启动");
        Checker checker = new Checker(result);
        Thread thread = new Thread(checker, "状态检测线程");
        thread.start();
    }

    /**
     * 运行方法
     * 
     */
    @Override
    public void run() {
        int confirmNumber = 0;
        while (true) {
            try {
                Thread.sleep(cd);
            } catch (InterruptedException ex) {
                Logger.info(ex.getMessage());
                return;
            }
            int size = Worker.size();
            if (confirmNumber >= CONFIRM_NUMBER_MAX) {
                Logger.info("正在关闭工作线程");
                Worker.shutdown();
                Logger.info("状态检测线程关闭");
                long endTime = System.currentTimeMillis();
                Report report = Report.getInstance();
                int total = report.getTotal();
                Logger.info(String.format("样本大小 = %s (个), 并发数 = %s (个), 响应时间 = %s/%s/%.2f (最小/最大/平均 ms), 吞吐量 = %.2f (个/秒), 错误率 = %.2f 百分号",
                        total,
                        report.getConcurrent(),
                        report.getMinCostTime(),
                        report.getMaxCostTime(),
                        report.getConcurrenceCostTime().get() * 1.0 / total,
                        (total * 1.0 / (endTime - report.getStartTime())) * 1000,
                        (report.getErrorNumber() * 1.0 / total) * 100));
                if (result != null) {
                    result.output(report.getConcurrent(), total, report.getMessageSize(), report.getMinCostTime(), report.getMaxCostTime(), report.getConcurrenceCostTime().get() * 1.0 / total, (total * 1.0 / (endTime - report.getStartTime())) * 1000, (report.getErrorNumber() * 1.0 / total) * 100);
                }
                return;
            }
            if (size == 0) {
                confirmNumber++;
            }
            Logger.info("飘过");
        }
    }
}
