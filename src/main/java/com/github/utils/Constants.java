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

/**
 * 相关静态常量类
 *
 * @author gao_xianglong@sina.com
 * @version 1.1-SNAPSHOT
 * @date created in 2021/1/16 10:56 下午
 */
public class Constants {
    /**
     * 加密时缺省每次读取10MB
     */
    public static int DEFAULT_ENCRYPT_CONTENT_SIZE = 0xa00000;
    /**
     * 解密时缺省每次读取13981024bytes
     */
    public static int DEFAULT_DECRYPT_CONTENT_SIZE = 0xd55560;
    public static final String KEY_ALGORITHM = "DESede";
    /**
     * 基于3DES加密算法
     */
    public static final String DEFAULT_CIPHER_ALGORITHM = "DESede/ECB/PKCS5Padding";
    public static final String ALGORITHM = "SHA1PRNG";
    public static final String CHARSET = "utf-8";
    /**
     * 加密文件缺省后缀
     */
    public static String DEFAULT_SUFFIX = ".dog";
    /**
     * 进度条总长度
     */
    public static int TOTLE_LENGTH = 100;
    /**
     * .dog文件魔术
     */
    public static int MAGIC_NUMBER = 0x19890225;
    /**
     * targetpath为空时使用临时目录
     */
    public static String DEFAULT_TARGET_PATH = System.getProperty("java.io.tmpdir");
    /**
     * 魔术长度4bytes
     */
    public static int MAGIC_NUMBER_SIZE = 4;
    public static String VERSION = "1.3-SNAPSHOT";
    public static final String LINE = System.getProperty("line.separator");
}
