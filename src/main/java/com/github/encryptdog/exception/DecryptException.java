/*
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.github.encryptdog.exception;

/**
 * @author jiushu
 * @version DecryptException.java, v 0.1 2022年01月21日 11:34 上午 jiushu
 */
public class DecryptException extends OperationException{
    public DecryptException() {
    }

    public DecryptException(String msg) {
        super(msg);
    }

    public DecryptException(Throwable e) {
        super(e);
    }

    public DecryptException(String msg, Throwable e) {
        super(msg, e);
    }
}