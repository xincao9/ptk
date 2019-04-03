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
package com.github.xincao9.ptk.core.interfaces;

import com.github.xincao9.ptk.core.model.Report;
import com.github.xincao9.ptk.core.util.Logger;
import java.util.ArrayList;

/**
 *
 * @author xincao9@gmail.com
 */
public abstract class Method {

    abstract public void exec(Object params) throws Exception;

    public final void run(Object params) {
        long startTime = System.currentTimeMillis();
        try {
            exec(params);
        } catch (Exception ex) {
            Report.getInstance().addErrorNumber(1);
            Logger.info(String.format("exception : %s", ex.getMessage()));
        } finally {
            long costTime = System.currentTimeMillis() - startTime;
            if (costTime > 1000) {
                if (params instanceof ArrayList) {
                    Logger.info(String.format("warn params = %s time = %s", ((ArrayList<String>) params).toString(), costTime));
                } else {
                    Logger.info(String.format("warn params = %s time = %s", params.toString(), costTime));
                }
            }
            Report.getInstance().changeMaxCostTime(costTime);
            Report.getInstance().changeMinCostTime(costTime);
            Report.getInstance().addConcurrenceCostTime(costTime);
        }
    }
}
