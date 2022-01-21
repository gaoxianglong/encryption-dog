/*
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.github.encryptdog.exception;

/**
 * @author jiushu
 * @version DogException.java, v 0.1 2022年01月21日 11:30 上午 jiushu
 */
public class DogException extends Exception {
    public static final long serialVersionUID = -1387516993124229921L;

    public DogException() {
        super();
    }

    public DogException(String msg) {
        super(msg);
    }

    public DogException(Throwable e) {
        super(e);
    }

    public DogException(String msg, Throwable e) {
        super(msg, e);
    }
}