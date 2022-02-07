/*
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.github.encryptdog.core;

import com.github.encryptdog.exception.DecryptException;
import com.github.encryptdog.exception.DogException;
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
import java.util.Base64;

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
    protected <T> void checkMagicNumber(T stream) throws OperationException {
        var in = (BufferedInputStream) stream;
        var mn = new byte[Constants.MAGIC_NUMBER_SIZE];
        try {
            in.read(mn);
        } catch (Throwable e) {
            throw new OperationException(e.getMessage(), e);
        }
        if (Utils.bytes2Int(mn) != Constants.MAGIC_NUMBER) {
            throw new OperationException("The target file is not a dog file,magic number:0x19890225");
        }
    }

    @Override
    protected int getDefaultSize(long available) {
        return available < Constants.DEFAULT_DECRYPT_CONTENT_SIZE ?
                (int) available : Constants.DEFAULT_DECRYPT_CONTENT_SIZE;
    }

    @Override
    protected <T> void bind(T stream) throws OperationException {
        var in = (BufferedInputStream) stream;
        var temp = new byte[Constants.UUID_FLAG_SIZE];
        try {
            in.read(temp);
            var size = Utils.bytes2Int(temp);
            if (size < 1) {
                return;
            }
            temp = new byte[size];
            // 读取物理设备的uuid
            in.read(temp);
            var uuid = new String(Utils.toBase64Decode(temp), Constants.CHARSET);
            if (!Utils.getUUID().equals(uuid)) {
                throw new OperationException("The UUID does not match,Please decrypt on the same physical device");
            }
        } catch (Throwable e) {
            throw new OperationException(e.getMessage(), e);
        }
    }

    @Override
    protected void write(byte[] content, int defaultSize, long available,
                         BufferedInputStream in, BufferedOutputStream out) throws OperationException {
        var len = -1;
        var count = len;
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
                var result = decrypt(content);
                out.write(result, 0, result.length);
                out.flush();
                count += len;
                // 输出加密的预计耗时
                if (visibleFlag) {
                    var end = System.currentTimeMillis();
                    Utils.printTimeConsuming(available, (double) (end - begin) / 1000,
                            Constants.DEFAULT_DECRYPT_CONTENT_SIZE);
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
        // 后缀为.dog表示为加密文件
        if (!file.getName().endsWith(Constants.DEFAULT_SUFFIX)) {
            throw new NameParseException("File suffix must be .dog");
        }
        return file.getName().substring(0, file.getName().length() - Constants.DEFAULT_SUFFIX.length());
    }

    /**
     * 数据解密
     *
     * @param data
     * @return
     * @throws DecryptException
     */
    private byte[] decrypt(byte[] data) throws DecryptException {
        try {
            var dc = Cipher.getInstance(Constants.DEFAULT_CIPHER_ALGORITHM);
            dc.init(Cipher.DECRYPT_MODE, getSecretKey(param.getSecretKey()));
            return dc.doFinal(Utils.toBase64Decode(data));
        } catch (Throwable e) {
            throw new DecryptException("The key is incorrect,Try Again", e);
        }
    }
}
