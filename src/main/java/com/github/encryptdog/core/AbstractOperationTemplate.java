/*
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.github.encryptdog.core;

import com.github.encryptdog.exception.DogException;
import com.github.encryptdog.exception.NameParseException;
import com.github.encryptdog.exception.OperationException;
import com.github.encryptdog.view.ParamDTO;
import com.github.utils.Constants;
import com.github.utils.Utils;

import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.SecureRandom;

/**
 * 数据加/解密模板抽象类
 *
 * @author jiushu
 * @version AbstractOperationTemplate.java, v 0.1 2022年01月19日 3:59 下午 jiushu
 */
public abstract class AbstractOperationTemplate {
    protected ParamDTO param;
    protected String targetPath;

    public AbstractOperationTemplate(ParamDTO param) {
        this.param = param;
    }

    /**
     * 执行加解密操作
     *
     * @throws DogException
     */
    public void execute() throws DogException {
        var fileName = param.getSourceFile();
        var file = new File(fileName);
        var isEncrypt = param.isEncrypt();
        // 加/解密文件的后缀检测与拼接
        fileName = checkSourceFile(file, fileName, isEncrypt);
        targetPath = String.format("%s/%s", param.getTargetPath(), fileName);
        try (var in = new BufferedInputStream(new FileInputStream(param.getSourceFile()));
             var out = new BufferedOutputStream(new FileOutputStream(targetPath))) {
            // 文件总大小，计算百分比进度条时需要使用
            var available = file.length();
            if (available < 1) {
                throw new OperationException("There is nothing in the target file");
            }
            // 获取每次读取的文件内容大小
            var defaultSize = getDefaultSize(available);
            var content = new byte[defaultSize];
            var stream = isEncrypt ? out : in;
            var begin = System.currentTimeMillis();
            // 魔术检测,如果是加密操作,则在文件起始位写入u4/32bit魔术码
            checkMagicNumber(stream);
            // 设置是否仅限在相同的物理设备上完成加/解密操作
            bind(stream);
            // 将加/解密内容写入目标文件
            write(content, defaultSize, available, in, out);
            print(available, begin);
            deleteSource();
        } catch (Throwable e) {
            throw new OperationException(e.getMessage(), e);
        } finally {
            System.out.println();
        }
    }

    /**
     * 输出单次操作结果
     *
     * @param available
     * @param begin
     * @throws OperationException
     */
    protected abstract void print(long available, long begin) throws OperationException;

    /**
     * 魔术检查
     *
     * @param stream
     * @param <T>
     * @throws OperationException
     */
    protected abstract <T> void checkMagicNumber(T stream) throws OperationException;

    /**
     * 获取分段操作容量
     *
     * @param available
     * @return
     */
    protected abstract int getDefaultSize(long available);

    /**
     * 是否仅限在相同的物理设备上完成加/解密操作
     *
     * @param stream
     * @param <T>
     * @throws OperationException
     */
    protected abstract <T> void bind(T stream) throws OperationException;

    /**
     * 压缩/解压缩操作
     *
     * @param source
     * @param target
     * @throws OperationException
     */
    protected abstract void compress(String source, String target) throws OperationException;

    /**
     * 向目标文件执行写入
     *
     * @param content
     * @param defaultSize
     * @param available
     * @param in
     * @param out
     * @throws OperationException
     */
    protected abstract void write(byte[] content, int defaultSize, long available,
                                  BufferedInputStream in, BufferedOutputStream out) throws OperationException;

    /**
     * 拼接目标文件全限定名
     *
     * @param file
     * @return
     * @throws NameParseException
     */
    protected abstract String splicTargetFileName(File file) throws NameParseException;

    /**
     * 设置压缩文件路径
     *
     * @param path
     * @return
     */
    protected String getCompressPath(String path) {
        return String.format("%s%s", path.substring(0, path.lastIndexOf(".")), ".zip");
    }

    /**
     * 返回秘钥器
     *
     * @param key
     * @return
     * @throws OperationException
     */
    protected SecretKeySpec getSecretKey(char[] key) throws OperationException {
        SecretKeySpec result = null;
        try {
            var random = SecureRandom.getInstance(Constants.ALGORITHM);
            random.setSeed(Utils.toBytes(key));
            // 获取秘钥生成器
            var kg = KeyGenerator.getInstance(Constants.KEY_ALGORITHM);
            kg.init(random);
            // 生成秘钥
            var generateKey = kg.generateKey();
            result = new SecretKeySpec(generateKey.getEncoded(), Constants.KEY_ALGORITHM);
        } catch (Throwable e) {
            throw new OperationException(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 加/解密文件的后缀检测与拼接
     *
     * @param file
     * @param fileName
     * @param isEncrypt
     * @return
     * @throws OperationException
     * @throws NameParseException
     */
    private String checkSourceFile(File file, String fileName, boolean isEncrypt) throws OperationException, NameParseException {
        if (!file.exists()) {
            throw new OperationException(String.format("file %s does not exist", fileName));
        }
        if (file.isFile()) {
            fileName = splicTargetFileName(file);
        } else {
            throw new OperationException("Please enter the correct file path");
        }
        return fileName;
    }

    /**
     * 删除源文件
     */
    private void deleteSource() {
        if (param.isDelete()) {
            new DelSource().del(param.getSourceFile());
        }
    }
}
