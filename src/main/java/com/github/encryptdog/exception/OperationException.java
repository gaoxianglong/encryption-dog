/*
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.github.encryptdog.exception;

/**
 * @author jiushu
 * @version OperationException.java, v 0.1 2022年01月21日 11:33 上午 jiushu
 */
public class OperationException extends DogException{
    public OperationException() {
    }

    public OperationException(String msg) {
        super(msg);
    }

    public OperationException(Throwable e) {
        super(e);
    }

    public OperationException(String msg, Throwable e) {
        super(msg, e);
    }
}