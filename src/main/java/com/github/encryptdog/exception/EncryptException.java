/*
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.github.encryptdog.exception;

/**
 * @author jiushu
 * @version EncryptException.java, v 0.1 2022年01月21日 11:34 上午 jiushu
 */
public class EncryptException extends OperationException{
    public EncryptException() {
    }

    public EncryptException(String msg) {
        super(msg);
    }

    public EncryptException(Throwable e) {
        super(e);
    }

    public EncryptException(String msg, Throwable e) {
        super(msg, e);
    }
}