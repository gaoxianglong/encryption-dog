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

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * @author gao_xianglong@sina.com
 * @version 0.1-SNAPSHOT
 * @date created in 2021/1/17 12:05 上午
 */
public class Utils {
    public static void printSchedule(double value) {
        var percent = (int) value;
        for (var i = 0; i < Constants.TOTLE_LENGTH + 10; i++) {
            System.out.print("\b");
        }
        System.out.print("[");
        var now = Constants.TOTLE_LENGTH * percent / 100;
        for (var i = 0; i < now; i++) {
            System.out.print("=");
        }
        System.out.print(">");
        for (var i = 0; i < Constants.TOTLE_LENGTH - now; i++) {
            System.out.print(" ");
        }
        System.out.print("]");
        System.out.print(String.format(" %s", percent >= 99 ? 100 : percent + "%"));
    }
}
