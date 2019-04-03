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

import java.util.concurrent.atomic.AtomicLong;

/**
 * 压测报告
 * 
 * @author xincao9@gmail.com
 */
public class Report {

    private int concurrent; // 并发数
    private int total; // 总请求数
    private AtomicLong concurrenceCostTime = new AtomicLong(0); // 并行花费的时间和
    private long startTime; // 自然时间
    private int errorNumber; // 错误数
    private long minCostTime = Integer.MAX_VALUE; // 最小花费时间
    private long maxCostTime = Integer.MIN_VALUE; // 最大花费时间
    private int messageSize = -1;

    private static Report report;

    private Report() {
    }

    public static Report getInstance() {
        if (report == null) {
            report = new Report();
        }
        return report;
    }

    public int getConcurrent() {
        return concurrent;
    }

    public void setConcurrent(int concurrent) {
        this.concurrent = concurrent;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public AtomicLong getConcurrenceCostTime() {
        return concurrenceCostTime;
    }

    public void setConcurrenceCostTime(AtomicLong concurrenceCostTime) {
        this.concurrenceCostTime = concurrenceCostTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getErrorNumber() {
        return errorNumber;
    }

    public void setErrorNumber(int errorNumber) {
        this.errorNumber = errorNumber;
    }

    public long addConcurrenceCostTime(long n) {
        return concurrenceCostTime.addAndGet(n);
    }

    public int addErrorNumber(int n) {
        return errorNumber += n;
    }

    public long getMinCostTime() {
        return minCostTime;
    }

    public void setMinCostTime(long minCostTime) {
        this.minCostTime = minCostTime;
    }

    public void changeMinCostTime(long minCostTime) {
        if (minCostTime < this.minCostTime) {
            this.minCostTime = minCostTime;
        }
    }

    public long getMaxCostTime() {
        return maxCostTime;
    }

    public void setMaxCostTime(long maxCostTime) {
        this.maxCostTime = maxCostTime;
    }

    public void changeMaxCostTime(long maxCostTime) {
        if (maxCostTime > this.maxCostTime) {
            this.maxCostTime = maxCostTime;
        }
    }

    public int getMessageSize() {
        return messageSize;
    }

    public void setMessageSize(int messageSize) {
        this.messageSize = messageSize;
    }
}
