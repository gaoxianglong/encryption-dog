/*
 * Copyright 2019-2119 gao_xianglong@sina.com
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
package com.github.test.encryptdog.core;

import com.github.encryptdog.exception.OperationException;
import com.github.utils.IdWorker;
import com.github.utils.SnowflakeWorker;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

/**
 * @author gao_xianglong@sina.com
 * @version 0.1-SNAPSHOT
 * @date created in 2022/5/21 23:52
 */
public class IdWorkerTest {
    private IdWorker<Long> idWorker;

    @Before
    public void init() {
        try {
            idWorker = new SnowflakeWorker(10, 10);
        } catch (OperationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testnextId() {
        var size = 100000;
        var list = new ArrayList<Long>() {{
            for (int i = 0; i < size; i++) {
                try {
                    add(idWorker.nextId());
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        }};
        Assert.assertEquals(size, list.size());
    }
}
