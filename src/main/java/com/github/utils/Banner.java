/*
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.github.utils;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * @author jiushu
 * @version Banner.java, v 0.1 2022年01月18日 3:35 下午 jiushu
 */
public class Banner {
    private static final String[] BANNER = {
            "   ____                       __  _           ___           ",
            "  / __/__  __________ _____  / /_(_)__  ___  / _ \\___  ___ _",
            " / _// _ \\/ __/ __/ // / _ \\/ __/ / _ \\/ _ \\/ // / _ \\/ _ `/",
            "/___/_//_/\\__/_/  \\_, / .__/\\__/_/\\___/_//_/____/\\___/\\_, / ",
            "                 /___/_/                             /___/  "};

    public static void print() {
        final var LINE = Constants.LINE;
        final var NUMBER = new AtomicInteger(0);
        var bannerBuf = new StringBuilder();
        bannerBuf.append(String.format("Welcome to %s", LINE));
        Stream.of(BANNER).forEach(x -> {
            bannerBuf.append(NUMBER.incrementAndGet() >= BANNER.length ? x : String.format("%s%s", x, LINE));
        });
//        bannerBuf.append(String.format("\tversion: %s%s",
//                Optional.ofNullable(System.getProperty("version")).orElseGet(() -> "unknown version"), LINE));
        bannerBuf.append(String.format("\n\tversion: %s%s",
                Constants.VERSION, LINE));
        System.out.println(bannerBuf.toString());
    }

    public static void main(String[] args) {
        print();
    }
}