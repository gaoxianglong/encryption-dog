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
import com.github.utils.Utils;

import javax.crypto.Cipher;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;

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
        return available < Constants.DEFAULT_ENCRYPT_CONTENT_SIZE ?
                (int) available : Constants.DEFAULT_ENCRYPT_CONTENT_SIZE;
    }

    @Override
    protected <T> void bind(T stream) throws OperationException {
        var out = (BufferedOutputStream) stream;
        try {
            if (param.isOnlyLocal()) {
                // 获取物理设备唯一标识,并进行base64加密
                var uuid = Utils.toBase64Encode(Utils.getUUID().getBytes(Constants.CHARSET)).getBytes(Constants.CHARSET);
                var size = Utils.int2Bytes(uuid.length);
                out.write(size, 0, size.length);
                out.write(uuid, 0, uuid.length);
            } else {
                var size = Utils.int2Bytes(0);
                out.write(size, 0, size.length);
            }
            out.flush();
        } catch (Throwable e) {
            throw new OperationException(e.getMessage(), e);
        }
    }

    @Override
    protected void write(byte[] content, int defaultSize, long available,
                         BufferedInputStream in, BufferedOutputStream out) throws OperationException {
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
                    Utils.printTimeConsuming(available, (double) (end - begin) / 1000,
                            Constants.DEFAULT_ENCRYPT_CONTENT_SIZE);
                    visibleFlag = false;
                }
                // 输出进度条
                Tooltips.printSchedule((double) count / available * 100);
            }
        } catch (Throwable e) {
            throw new OperationException(e.getMessage(), e);
        }
    }

    @Override
    protected String splicTargetFileName(File file) throws NameParseException {
        // 源文件后缀拼接.dog表示为加密文件
        return String.format("%s%s", file.getName(), Constants.DEFAULT_SUFFIX);
    }

    /**
     * 数据加密
     *
     * @param data
     * @return
     * @throws EncryptException
     */
    private byte[] encrypt(byte[] data) throws EncryptException {
        try {
            var ec = Cipher.getInstance(Constants.DEFAULT_CIPHER_ALGORITHM);
            ec.init(Cipher.ENCRYPT_MODE, getSecretKey(param.getSecretKey()));
            // 执行数据加密后再base64编码
            return Utils.toBase64Encode(ec.doFinal(data)).getBytes(Constants.CHARSET);
        } catch (Throwable e) {
            throw new EncryptException(String.format("Encryption failed,secret-key:%s", new String(param.getSecretKey())), e);
        }
    }
}
