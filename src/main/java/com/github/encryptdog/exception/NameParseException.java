/*
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.github.encryptdog.exception;

/**
 * @author jiushu
 * @version NameParseException.java, v 0.1 2022年01月21日 11:32 上午 jiushu
 */
public class NameParseException extends DogException {
    public NameParseException() {
    }

    public NameParseException(String msg) {
        super(msg);
    }

    public NameParseException(Throwable e) {
        super(e);
    }

    public NameParseException(String msg, Throwable e) {
        super(msg, e);
    }
}