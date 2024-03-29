/*
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.github.encryptdog.core;

import com.github.encryptdog.exception.EncryptException;
import com.github.encryptdog.exception.NameParseException;
import com.github.encryptdog.exception.OperationException;
import com.github.encryptdog.view.ParamDTO;
import com.github.encryptdog.view.Tooltips;
import com.github.utils.Constants;
import com.github.utils.RamdomWorker;
import com.github.utils.SnowflakeWorker;
import com.github.utils.Utils;

import javax.crypto.Cipher;
import java.io.*;
import java.util.Objects;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 数据加密操作
 *
 * @author jiushu
 * @version DataEncrypt.java, v 0.1 2022年01月19日 4:03 下午 jiushu
 */
public class DataEncrypt extends AbstractOperationTemplate {
    public DataEncrypt(ParamDTO param) {
        super(param);
    }

    /**
     * 原秘钥
     */
    private char[] osk;
    /**
     * 随机秘钥
     */
    private String rsk;

    @Override
    protected void authentication() throws OperationException {
        createStoreFile();
        if (!param.isOnlyLocal()) {
            clear();
            return;
        }
        try {
            // 获取原秘钥
            osk = param.getSecretKey();
            // 生成随机秘钥
            rsk = createSecretKey();
            // 使用随机秘钥加密文件
            param.setSecretKey(rsk.toCharArray());
        } catch (Throwable e) {
            throw new OperationException(e.getMessage(), e);
        }
    }

    /**
     * 固化随机秘钥至dp文件
     *
     * @throws OperationException
     */
    private void storeRSK() throws OperationException {
        if (!param.isOnlyLocal()) {
            return;
        }
        try (var in = new BufferedInputStream(new FileInputStream(Constants.STORE_PWD_FILE_PATH))) {
            var properties = new Properties();
            properties.load(in);
            // 获取RSK(文件的真实加密秘钥)
            if (!new File(targetFile).exists()) {
                return;
            }
            var key = String.valueOf(f_uuid);
            // 使用原秘钥加密随机秘钥
            var temp = new String(encrypt(rsk.getBytes(Constants.CHARSET), osk), Constants.CHARSET);
            properties.put(key, temp);
            // 将RSK固化到本地
            properties.store(new BufferedOutputStream(new FileOutputStream(Constants.STORE_PWD_FILE_PATH)), null);
        } catch (Throwable e) {
            throw new OperationException(e.getMessage(), e);
        }
    }

    /**
     * 重复加密相同的目标文件时,如果未开启only-local命令时,解除已存在的物理绑定
     *
     * @throws OperationException
     */
    private void clear() throws OperationException {
        try (var in = new BufferedInputStream(new FileInputStream(Constants.STORE_PWD_FILE_PATH))) {
            var properties = new Properties();
            properties.load(in);
            properties.remove(targetFile);
            properties.store(new BufferedOutputStream(new FileOutputStream(Constants.STORE_PWD_FILE_PATH)), null);
        } catch (Throwable e) {
            throw new OperationException(e.getMessage(), e);
        }
    }

    /**
     * 创建高安全性密码
     *
     * @return
     * @throws Throwable
     */
    private String createSecretKey() throws Throwable {
        return new RamdomWorker().nextId();
    }

    @Override
    protected void print(long available, long begin) throws OperationException {
        var beforeSize = Utils.capacityFormat(available);
        var afterSize = Utils.capacityFormat(new File(targetFile).length());
        var tp = targetFile;
        // 是否启用压缩操作
        if (param.getCompress()) {
            targetFile = String.format("%s.zip", Utils.cancelFileSuffix(targetFile, 2));
            compress(tp, targetFile);
            afterSize = Utils.capacityFormat(new File(targetFile).length());
            new DelSource().del(tp);
        }
        var end = System.currentTimeMillis();
        var tc = Utils.timeFormat((end - begin) / 1000);
        Tooltips.print(Tooltips.Number._5, tc, beforeSize, afterSize, targetFile);
        // 当设置启动参数-Dstore=true时,将会在临时目录下固化base64秘钥
        try {
            new StoreUserSecretKey().store(param, beforeSize, targetFile, afterSize, osk);
        } catch (IOException e) {
            throw new OperationException(e.getMessage(), e);
        }
    }

    @Override
    protected <T> void checkMagicNumber(T stream) throws OperationException {
        var out = (BufferedOutputStream) stream;
        var mn = Utils.int2Bytes(Constants.MAGIC_NUMBER);
        try {
            // 文件起始位写入u4/32bit魔术码
            out.write(mn, 0, mn.length);
            out.flush();
        } catch (Throwable e) {
            throw new OperationException(e.getMessage(), e);
        }
    }

    @Override
    protected int getDefaultSize(long available) {
        return available < Constants.DEFAULT_ENCRYPT_CONTENT_SIZE ? (int) available : Constants.DEFAULT_ENCRYPT_CONTENT_SIZE;
    }

    @Override
    protected <T> void bind(T stream) throws OperationException {
        var out = (BufferedOutputStream) stream;
        try {
            if (param.isOnlyLocal()) {
                // 获取hardwareUUID
                var hardwareUUID = Utils.toBase64Encode(Utils.getUUID().getBytes(Constants.CHARSET)).getBytes(Constants.CHARSET);
                out.write(new byte[] { (byte) (hardwareUUID.length & 0xff) });
                out.write(hardwareUUID, 0, hardwareUUID.length);
                // 获取sid
                f_uuid = ((SnowflakeWorker) param.getIdWorker()).nextId();
                // 记录f_uuid回查用
                param.setFileId(f_uuid);
                out.write(Utils.long2Bytes(f_uuid));
            } else {
                out.write(new byte[Constants.UUID_BYTES]);
            }
            out.flush();
        } catch (Throwable e) {
            throw new OperationException("Device binding failed", e);
        }
    }

    @Override
    protected void compress(String source, String target) throws OperationException {
        Tooltips.print(Tooltips.Number._9);
        try (var in = new BufferedInputStream(new FileInputStream(source)); var out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(target)))) {
            out.putNextEntry(new ZipEntry(source.substring(source.lastIndexOf(Constants.SEPARATOR) + 1)));
            var available = new File(source).length();
            var content = new byte[Constants.DEFAULT_ENCRYPT_CONTENT_SIZE];
            var len = -1;
            long count = len;
            while ((len = in.read(content)) != -1) {
                out.write(content, 0, len);
                out.flush();
                count += len;
                // 输出进度条
                Tooltips.printSchedule((double) count / available * 100);
            }
            // 确保最终进度条最终能够追加到100%
            Tooltips.printSchedule(100);
        } catch (Throwable e) {
            throw new OperationException("File compression failed", e);
        }
    }

    @Override
    protected void write(byte[] content, int defaultSize, long available, BufferedInputStream in, BufferedOutputStream out) throws OperationException {
        var len = -1;
        long count = len;
        // 显示预计耗时标识
        var visibleFlag = true;
        var begin = System.currentTimeMillis();
        try {
            while ((len = in.read(content)) != -1) {
                if (len < defaultSize) {
                    var temp = new byte[len];
                    System.arraycopy(content, 0, temp, 0, len);
                    content = temp;
                }
                var result = encrypt(content);
                out.write(result, 0, result.length);
                out.flush();
                count += len;
                // 输出加密的预计耗时
                if (visibleFlag) {
                    var end = System.currentTimeMillis();
                    Utils.printTimeConsuming(available, (double) (end - begin) / 1000, Constants.DEFAULT_ENCRYPT_CONTENT_SIZE);
                    visibleFlag = false;
                }
                // 输出进度条
                Tooltips.printSchedule((double) count / available * 100);
            }
            // 确保最终进度条最终能够追加到100%
            Tooltips.printSchedule(100);
            storeRSK();
        } catch (Throwable e) {
            throw new OperationException(e.getMessage(), e);
        }
    }

    /**
     * 加/解密文件的后缀检测与拼接
     * @param file
     * @return
     * @throws NameParseException
     */
    @Override
    protected String splicTargetFileName(File file) throws NameParseException {
        // 获取源文件文件名
        var dn = file.getName();
        // 获取源文件后缀,如果不存在后缀为返回""
        var suffix = Utils.getFileSuffix(dn);
        // 获取显示设定的文件名
        var n = param.getName();
        // 如果没有显式设置文件名则用源文件的名称,反之使用自定义文件名+原文件后缀
        var fileName = Objects.isNull(n) ? dn : String.format("%s%s", n, suffix);
        // 源文件后缀拼接.dog表示为加密文件,
        return String.format("%s%s", fileName, Constants.DEFAULT_SUFFIX);
    }

    /**
     * 数据加密
     *
     * @param data
     * @return
     * @throws EncryptException
     */
    private byte[] encrypt(byte[] data) throws EncryptException {
        return encrypt(data, param.getSecretKey());
    }

    private byte[] encrypt(byte[] data, char[] key) throws EncryptException {
        try {
            var ec = Cipher.getInstance(Constants.DEFAULT_CIPHER_ALGORITHM);
            ec.init(Cipher.ENCRYPT_MODE, getSecretKey(key));
            // 执行数据加密后再base64编码
            return Utils.toBase64Encode(ec.doFinal(data)).getBytes(Constants.CHARSET);
        } catch (Throwable e) {
            throw new EncryptException(String.format("Encryption failed,secret-key:%s", new String(param.getSecretKey())), e);
        }
    }
}
