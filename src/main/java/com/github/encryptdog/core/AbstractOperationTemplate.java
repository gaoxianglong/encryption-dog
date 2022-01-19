/*
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.github.encryptdog.core;

import com.github.encryptdog.view.ParamDTO;
import com.github.utils.Constants;
import com.github.utils.Utils;

import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/**
 * 数据加/解密模板抽象类
 *
 * @author jiushu
 * @version AbstractOperationTemplate.java, v 0.1 2022年01月19日 3:59 下午 jiushu
 */
public abstract class AbstractOperationTemplate {
    protected Base64.Decoder decoder = Base64.getDecoder();
    protected Base64.Encoder encoder = Base64.getEncoder();
    protected ParamDTO param;

    public AbstractOperationTemplate(ParamDTO param) {
        this.param = param;
    }

    /**
     * 执行加解密操作
     *
     * @throws Throwable
     */
    public void execute() throws Throwable {
        var begin = System.currentTimeMillis();
        var fileName = param.getSourceFile();
        var file = new File(fileName);
        var isEncrypt = param.isEncrypt();
        // 加/解密文件的后缀检测与拼接
        fileName = checkSourceFile(file, fileName, isEncrypt);
        var targetPath = String.format("%s/%s", param.getTargetPath(), fileName);
        try (var in = new BufferedInputStream(new FileInputStream(param.getSourceFile()));
             var out = new BufferedOutputStream(new FileOutputStream(targetPath))) {
            // 文件总大小，计算百分比进度条时需要使用
            var available = file.length();
            if (available < 1) {
                throw new Exception("There is nothing in the target file");
            }
            // 获取每次读取的文件内容大小
            int defaultSize = getDefaultSize(available);
            var content = new byte[defaultSize];
            System.out.println("Please wait...");
            // 魔术检测,如果是加密操作,则在文件起始位写入u4/32bit魔术码
            checkMagicNumber(isEncrypt ? out : in);
            // 将加/解密内容写入目标文件
            write(content, defaultSize, available, in, out);
            // 确保最终进度条最终能够追加到100%
            Utils.printSchedule(100);
            var end = System.currentTimeMillis();
            var tc = (double) (end - begin) / 1000;
            System.out.println(String.format("\n\n%s\tsuccess\n[Time-consuming]:%.2f%s," +
                            "[Before size]:%.2fMB,[After size]:%.2fMB\n[Target path]:%s",
                    isEncrypt ? "Encrypt" : "Decrypt",
                    tc, tc >= 1 ? "s" : "ms", (double) available / 0X100000,
                    (double) new File(targetPath).length() / 0X100000, targetPath));
        } finally {
            Arrays.fill(param.getSecretKey(), ' ');
            // 操作结束后是否删除源文件
            if (param.isDelete()) {
                new File(param.getSourceFile()).delete();
            }
        }
    }

    /**
     * 魔术检查
     *
     * @param stream
     * @param <T>
     * @throws Throwable
     */
    protected abstract <T> void checkMagicNumber(T stream) throws Throwable;

    /**
     * 获取分段操作容量
     *
     * @param available
     * @return
     * @throws Throwable
     */
    protected abstract int getDefaultSize(long available) throws Throwable;

    /**
     * 向目标文件执行写入
     *
     * @param content
     * @param defaultSize
     * @param available
     * @param in
     * @param out
     * @throws Throwable
     */
    protected abstract void write(byte[] content, int defaultSize, long available,
                                  BufferedInputStream in, BufferedOutputStream out) throws Throwable;

    /**
     * 凭借目标文件全限定名
     *
     * @param file
     * @return
     * @throws Throwable
     */
    protected abstract String splicTargetFileName(File file) throws Throwable;

    /**
     * 返回秘钥器
     *
     * @param key
     * @return
     * @throws Throwable
     */
    protected SecretKeySpec getSecretKey(char[] key) throws Throwable {
        var random = SecureRandom.getInstance(Constants.ALGORITHM);
//        random.setSeed(key.getBytes(Constants.CHARSET));
        random.setSeed(Utils.toBytes(key));
        // 获取秘钥生成器
        var kg = KeyGenerator.getInstance(Constants.KEY_ALGORITHM);
        kg.init(random);
        // 生成秘钥
        var generateKey = kg.generateKey();
        return new SecretKeySpec(generateKey.getEncoded(), Constants.KEY_ALGORITHM);
    }

    /**
     * 加/解密文件的后缀检测与拼接
     *
     * @param file
     * @param fileName
     * @param isEncrypt
     * @return
     * @throws Throwable
     */
    private String checkSourceFile(File file, String fileName, boolean isEncrypt) throws Throwable {
        if (!file.exists()) {
            throw new Exception(String.format("file %s does not exist", fileName));
        }
        if (file.isFile()) {
            fileName = splicTargetFileName(file);
        } else {
            throw new Exception("Please enter the correct file path");
        }
        return fileName;
    }
}
