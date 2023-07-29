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
    protected String   targetPath;
    /**
     * 文件的身份ID，--only-local命令下使用
     */
    protected Long     f_uuid;

    public AbstractOperationTemplate(ParamDTO param) {
        this.param = param;
    }

    /**
     * 执行加解密操作
     *
     * @throws DogException
     */
    public boolean execute() throws DogException {
        var result = false;
        var fileName = param.getSourceFile();
        var file = new File(fileName);
        var isEncrypt = param.isEncrypt();
        // 加/解密文件的后缀检测与拼接
        fileName = checkSourceFile(file, fileName);
        targetPath = fileExists(String.format("%s%s", param.getTargetPath(), fileName));
        try (var in = new BufferedInputStream(new FileInputStream(param.getSourceFile())); var out = new BufferedOutputStream(new FileOutputStream(targetPath))) {
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
            // 如果开启only-local命令,则紧跟magic后面追加物理设备UUID
            bind(stream);
            // 如果开启only-local命令,则启动double secret key authentication
            authentication();
            // 将加/解密内容写入目标文件
            write(content, defaultSize, available, in, out);
            print(available, begin);
            deleteSource();
            result = true;
        } catch (Throwable e) {
            throw new OperationException(e.getMessage(), e);
        } finally {
            if (!result) {
                // 无论加解密是否成功,目标文件已经提前创建，如果操作失败则删除目标文件
                Utils.deleteFile(targetPath);
            }
            System.out.println();
        }
        return result;
    }

    /**
     * 创建真实秘钥固化文件
     *
     * @throws OperationException
     */
    protected void createStoreFile() throws OperationException {
        var file = new File(Constants.STORE_PWD_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(Constants.STORE_PWD_FILE_PATH);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Throwable e) {
                throw new OperationException(e.getMessage(), e);
            }
        }
    }

    /**
     * 检查目标文件是否存在，如果已存在则变更目标文件名称避免数据覆盖
     *
     * @param fp
     * @return
     */
    private String fileExists(String fp) {
        var file = new File(fp);
        if (!file.exists()) {
            return fp;
        }
        var suffix = Utils.getFileSuffix(fp);
        // 格式为name-时间戳.dog
        return String.format("%s-%s%s", Utils.cancelFileSuffix(fp, 1), System.nanoTime(), suffix);
    }

    /**
     * 开启最高安全性时启用
     *
     * @throws OperationException
     */
    protected abstract void authentication() throws OperationException;

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
    protected abstract void write(byte[] content, int defaultSize, long available, BufferedInputStream in, BufferedOutputStream out) throws OperationException;

    /**
     * 拼接目标文件全限定名
     *
     * @param file
     * @return
     * @throws NameParseException
     */
    protected abstract String splicTargetFileName(File file) throws NameParseException;

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
            var kg = KeyGenerator.getInstance(Constants.KEY_ALGORITHM);
            kg.init(random);
            var generateKey = kg.generateKey();

            // 当秘钥不足192bit时会自动补全,超出则截取前192bit数据
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
     * @return
     * @throws OperationException
     * @throws NameParseException
     */
    private String checkSourceFile(File file, String fileName) throws OperationException, NameParseException {
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
