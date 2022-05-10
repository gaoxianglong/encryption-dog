/*
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.github.encryptdog.core;

import com.github.encryptdog.exception.OperationException;
import com.github.encryptdog.view.ParamDTO;
import com.github.utils.Constants;
import com.github.utils.Utils;

import java.io.*;
import java.util.Objects;
import java.util.Properties;

/**
 * -Dstore=true开启时,固化对应加密文件的秘钥至操作系统的临时目录下
 *
 * @author jiushu
 * @version StoreSecretKey.java, v 0.1 2022年01月20日 2:36 下午 jiushu
 */
public class StoreUserSecretKey {
    /**
     * 固化秘钥
     *
     * @param param
     * @param ss
     * @param t
     * @param ts
     * @param osk
     * @throws IOException
     */
    protected void store(ParamDTO param, String ss, String t, String ts, char[] osk) throws IOException {
        Objects.requireNonNull(param);
        if (!param.isEncrypt()) {
            return;
        }
        if (!param.isStore()) {
            return;
        }
        var s = param.getSourceFile();
        // 获取用户秘钥,如果开启了only-local,所记录的秘钥也仅仅是用户秘钥而不是RSK
        var sk = osk;
        var file = new File(Constants.STORE_SK_PATH);
        if (!file.exists()) {
            file.createNewFile();
        }
        BufferedWriter out = null;
        // 转储文件格式:加密文件,源文件,源文件大小,目标文件,目标文件大小=秘钥
        try (var in = new BufferedReader(new FileReader(Constants.STORE_SK_PATH))) {
            var properties = new Properties();
            properties.load(in);
            var key = String.format("[source]:%s,[source-size]:%s," +
                    "[target]:%s,[target-size]:%s", s, ss, t, ts);
            properties.setProperty(key, Utils.toBase64Encode(new String(sk).getBytes(Constants.CHARSET)));
            //不再进行追加内容，避免重复
//          out = new BufferedWriter(new FileWriter(Constants.STORE_SK_PATH, true));
            out = new BufferedWriter(new FileWriter(Constants.STORE_SK_PATH));
            // 记录转储日志
            properties.store(out, null);
        } catch (Throwable e) {
            new OperationException("Secret key storage failed", e);
        } finally {
            if (Objects.nonNull(out)) {
                out.close();
            }
        }
    }
}
