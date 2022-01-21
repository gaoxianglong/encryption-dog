/*
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.github.encryptdog.core;

import com.github.encryptdog.exception.OperationException;
import com.github.encryptdog.view.ParamDTO;
import com.github.utils.Constants;
import com.github.utils.Utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Objects;
import java.util.Properties;

/**
 * -Dstore=true开启时,固化对应加密文件的秘钥至操作系统的临时目录下
 *
 * @author jiushu
 * @version StoreSecretKey.java, v 0.1 2022年01月20日 2:36 下午 jiushu
 */
public class StoreSecretKey {
    /**
     * 固化秘钥
     *
     * @param param
     * @param ss
     * @param t
     * @param ts
     * @throws OperationException
     */
    protected void store(ParamDTO param, double ss, String t, double ts) throws OperationException {
        Objects.requireNonNull(param);
        if (!param.isEncrypt()) {
            return;
        }
        if (!param.isStore()) {
            return;
        }
        var s = param.getSourceFile();
        var sk = param.getSecretKey();
        Properties properties = new Properties();
        // 转储文件格式:加密文件,源文件,源文件大小,目标文件,目标文件大小=秘钥
        try (var out = new BufferedWriter(new FileWriter(Constants.STORE_SK_PATH, true))) {
            var key = String.format("[dog-file]:%s,[source]:%s,[source-size]:%.2fMB," +
                    "[target]:%s,[target-size]:%.2fMB", t, s, ss, t, ts);
            properties.setProperty(key, Utils.toBase64Encode(new String(sk).getBytes(Constants.CHARSET)));
            properties.store(out, "store secret-key");
        } catch (Throwable e) {
            properties.clear();
            new OperationException("Secret key storage failed", e);
        }
    }
}
