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
package com.github.encryptdog.core;

import com.github.encryptdog.view.Tooltips;
import com.github.utils.Utils;

import java.io.File;
import java.util.Scanner;

/**
 * @author gao_xianglong@sina.com
 * @version 0.1-SNAPSHOT
 * @date created in 2022/2/25 10:39 下午
 */
public class DelSource {
    public boolean deleteConfirmation(boolean isDel) {
        if (!isDel) {
            return false;
        }
        Tooltips.print(Tooltips.Number._8);
        var temp = new Scanner(System.in).nextLine();
        if ("Y".equalsIgnoreCase(temp)) {
            return true;
        }
        return false;
    }

    public boolean del(String path) {
        return Utils.deleteFile(path);
    }
}
