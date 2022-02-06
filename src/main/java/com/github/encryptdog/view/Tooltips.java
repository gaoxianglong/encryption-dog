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
package com.github.encryptdog.view;

import com.github.utils.Constants;

/**
 * @author gao_xianglong@sina.com
 * @version 0.1-SNAPSHOT
 * @date created in 2022/2/7 1:19 上午
 */
public class Tooltips {
    public static void print(Number number, Object... str) {
        switch (number) {
            case _1 -> System.out.println("Source file list:");
            case _2 -> System.out.println("Please confirm whether it is these files [Y/N]:");
            case _3 -> System.out.println("Please wait...\n");
            case _4 -> System.out.println(String.format("[%s file number]:%s/%s\n[Source path]:%s",
                    (boolean) str[0] ? "Encrypt" : "Decrypt", str[1], str[2], str[3]));
            case _5 -> System.out.println(String.format("\n[%s result]:success\n[Time-consuming]:%.2f%s," +
                            "[Before size]:%.2fMB,[After size]:%.2fMB\n[Target path]:%s",
                    (boolean) str[0] ? "Encrypt" : "Decrypt",
                    str[1], (double) str[1] >= 1 ? "s" : "ms", str[2], str[3], str[4]));
            case _6 -> System.out.println(String.format(">>> Operation complete <<<\n[Total time]:%.2f%s\n[Results]:" +
                            "total files:%s,successes:%s,failures:%s",
                    str[0], (double) str[0] >= 1 ? "s" : "ms", str[1], (int) str[1] - (int) str[2], str[2]));
            case _7 ->  System.out.println(String.format("[SecretKey path]:%s", Constants.STORE_SK_PATH));
        }
    }

    public enum Number {
        _1, _2, _3, _4, _5, _6, _7
    }
}
