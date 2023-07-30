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

import java.io.File;

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
    public static final int    DEFAULT_ENCRYPT_CONTENT_SIZE = 0xa00000;
    /**
     * 解密时缺省每次读取13981024bytes
     */
    public static final int    DEFAULT_DECRYPT_CONTENT_SIZE = 0xd55560;
    public static final String KEY_ALGORITHM                = "DESede";
    /**
     * 基于3DES加密算法
     */
    public static final String DEFAULT_CIPHER_ALGORITHM     = "DESede/ECB/PKCS5Padding";
    public static final String ALGORITHM                    = "SHA1PRNG";
    public static final String CHARSET                      = "utf-8";
    public static final String VERSION                      = "1.6.0-RELEASE";
    /**
     * 加密文件缺省后缀
     */
    public static final String DEFAULT_SUFFIX               = ".dog";
    /**
     * 进度条总长度
     */
    public static final int    TOTLE_LENGTH                 = 100;
    /**
     * magic number
     */
    public static final int    MAGIC_NUMBER                 = 0x19890225;
    /**
     * targetpath为空时使用桌面路径
     */
    @Deprecated
    public static final String DEFAULT_TARGET_PATH          = System.getProperty("java.io.tmpdir");
    /**
     * 魔术长度4bytes
     */
    public static final int    MAGIC_NUMBER_BYTES           = 4;
    /**
     * UUID长度1bytes
     */
    public static final int    UUID_BYTES                   = 1;
    public static final int    FILE_ID_BYTES                = 8;
    public static final String LINE                         = System.getProperty("line.separator");
    /**
     * VM参数,加密操作时在系统临时目录下转储对应加密文件的秘钥信息,慎用
     */
    public static final String STORE                        = "dog-store";
    /**
     * 秘钥存储地址
     */
    public static final String STORE_SK_PATH                = String.format("%s/dog-secret-key.log", DEFAULT_TARGET_PATH);
    /**
     * 通配符
     */
    public static final char   WILDCARD                     = '*';
    /**
     * 路径分隔符
     */
    public static final String SEPARATOR                    = System.getProperty("file.separator");
    /**
     * 获取mac系统的硬件UUID命令
     */
    public final static String UUID_COMMAND                 = "system_profiler SPHardwareDataType";
    /**
     * 通配符匹配规则
     */
    public final static String WILDCARD_MATCHING_RULE       = "[\\\\u4e00-\\\\u9fa5\\\\w\\\\s-~@\\$#\\^&.\\(\\)\\《\\》\\（\\）\\—]{0,}";
    /**
     * 缺省用户桌面路径
     */
    public final static String DEFAULT_USER_DESKTOP_PATH    = String.format("%s%s%s%s", System.getProperty("user.home"), SEPARATOR, "Desktop", SEPARATOR);
    /**
     * 最高安全性密码存储目录地址
     */
    public static final String STORE_PWD_PATH               = String.format("%s%s.dog", System.getProperty("user.home"), SEPARATOR);
    /**
     * 最高安全性密码存储文件地址
     */
    public static final String STORE_PWD_FILE_PATH          = String.format("%s%sdp", STORE_PWD_PATH, SEPARATOR);
    /**
     * 一小时所对应的秒数
     */
    public static final int    HOUR                         = 0xe10;
    /**
     * 一分钟所对应的秒数
     */
    public static final int    MINUTE                       = 0x3c;
    /**
     * 1GB对应的字节大小
     */
    public static final int    _1GB                         = 0x40000000;
    /**
     * 1MB对应的字节大小
     */
    public static final int    _1MB                         = 0X100000;
    public static final long   IDC_ID                       = (long) (Math.random() * (~(-1L << 5L)));
    public static final long   WORKER_ID                    = (long) (Math.random() * (~(-1L << 5L)));
}
