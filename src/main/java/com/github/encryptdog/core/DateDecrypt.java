/*
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.github.encryptdog.core;

import com.github.encryptdog.view.ParamDTO;
import com.github.utils.Constants;
import com.github.utils.Utils;

import javax.crypto.Cipher;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;

/**
 * 数据解密操作
 *
 * @author jiushu
 * @version DateDecrypt.java, v 0.1 2022年01月19日 4:02 下午 jiushu
 */
public class DateDecrypt extends AbstractOperationTemplate {
    public DateDecrypt(ParamDTO param) {
        super(param);
    }

    @Override
    protected <T> void checkMagicNumber(T stream) throws Throwable {
        BufferedInputStream in = (BufferedInputStream) stream;
        var mn = new byte[Constants.MAGIC_NUMBER_SIZE];
        in.read(mn);
        if (Utils.bytes2Int(mn) != Constants.MAGIC_NUMBER) {
            throw new Exception("The target file is not a dog file,magic number:0x19890225");
        }
    }

    @Override
    protected int getDefaultSize(long available) throws Throwable {
        return available < Constants.DEFAULT_DECRYPT_CONTENT_SIZE ?
                (int) available : Constants.DEFAULT_DECRYPT_CONTENT_SIZE;
    }

    @Override
    protected void write(byte[] content, int defaultSize, long available,
                         BufferedInputStream in, BufferedOutputStream out) throws Throwable {
        var len = -1;
        var count = len;
        while ((len = in.read(content)) != -1) {
            if (len < defaultSize) {
                var temp = new byte[len];
                System.arraycopy(content, 0, temp, 0, len);
                content = temp;
            }
            var result = decrypt(content);
            out.write(result, 0, result.length);
            out.flush();
            count += len;
            Utils.printSchedule((double) count / available * 100);// 输出进度条
        }
    }

    @Override
    protected String splicTargetFileName(File file) throws Throwable {
        // 后缀为.dog表示为加密文件
        if (!file.getName().endsWith(Constants.DEFAULT_SUFFIX)) {
            throw new Exception("File suffix must be .dog");
        }
        return file.getName().substring(0, file.getName().length() - Constants.DEFAULT_SUFFIX.length());
    }

    /**
     * 数据解密
     *
     * @return
     */
    private byte[] decrypt(byte[] data) throws Throwable {
        try {
            var dc = Cipher.getInstance(Constants.DEFAULT_CIPHER_ALGORITHM);
            dc.init(Cipher.DECRYPT_MODE, getSecretKey(param.getSecretKey()));
            return dc.doFinal(decoder.decode(data));
        } catch (Throwable e) {
            throw new Exception("The key is incorrect,Try Again", e);
        }
    }
}
