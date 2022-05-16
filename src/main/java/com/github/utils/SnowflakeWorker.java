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
package com.github.utils;

import com.github.encryptdog.exception.OperationException;

/**
 * SnowflakeID生成器
 *
 * @author gao_xianglong@sina.com
 * @version 0.1-SNAPSHOT
 * @date created in 2022/5/14 16:15
 */
public class SnowflakeWorker implements IdWorker<Long> {
    /**
     * 时间纪元
     */
    private final long INIT_EPOCH = 1652697002988L;
    private final long WORKER_ID_BITS = 5L;
    private final long IDC_ID_BITS = 5L;
    private final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private final long MAX_IDC_ID = ~(-1L << IDC_ID_BITS);
    private final long SEQUENCE_BITS = 12L;
    /**
     * workerId左移12位
     */
    private final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    /**
     * idcId左移17位
     */
    private final long IDC_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    /**
     * 时间戳左移22位
     */
    private final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + IDC_ID_BITS;
    private final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);
    private long lastTimestamp = -1L;
    private long idcId;
    private long workerId;
    private long sequence;

    public SnowflakeWorker(long idcId, long workerId) throws OperationException {
        if (idcId < 0 || idcId > MAX_IDC_ID) {
            throw new OperationException("idcId input error");
        }
        if (workerId < 0 || workerId > MAX_WORKER_ID) {
            throw new OperationException("workerId input error");
        }
        this.idcId = idcId;
        this.workerId = workerId;
    }

    @Override
    public Long nextId() throws Throwable {
        return getSequenceId();
    }

    private synchronized long getSequenceId() throws OperationException {
        var ct = System.currentTimeMillis();
        if (ct < lastTimestamp) {
            throw new OperationException("");
        }
        if (ct == lastTimestamp) {
            sequence = (++sequence) & SEQUENCE_MASK;
            if (sequence == 0) {
                ct = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }
        lastTimestamp = ct;
        return ((ct - INIT_EPOCH) << TIMESTAMP_SHIFT) | (idcId << IDC_ID_SHIFT) | (workerId << WORKER_ID_SHIFT) | sequence;
    }

    private long tilNextMillis(long lastTimestamp) {
        var ct = System.currentTimeMillis();
        while (ct <= lastTimestamp) {
            ct = System.currentTimeMillis();
        }
        return ct;
    }
}
