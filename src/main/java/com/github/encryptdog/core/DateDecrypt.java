/*
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.github.encryptdog.core;

import com.github.encryptdog.exception.DecryptException;
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
import java.io.FileInputStream;
import java.util.Objects;
import java.util.Properties;

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
    protected void authentication() throws OperationException {
        createStoreFile();
        if (Objects.isNull(f_uuid)) {
            return;
        }
        try (var in = new BufferedInputStream(new FileInputStream(Constants.STORE_PWD_FILE_PATH))) {
            var properties = new Properties();
            properties.load(in);
            var key = String.valueOf(f_uuid);
            // 获取随机秘钥
            var rsk = properties.getProperty(key);
            if (Objects.isNull(rsk)) {
                return;
            }
            // 使用原秘钥解密对应的随机秘钥
            var sk = decrypt(rsk.getBytes(Constants.CHARSET), param.getSecretKey());
            param.setSecretKey(new String(sk, Constants.CHARSET).toCharArray());
        } catch (Throwable e) {
            throw new OperationException(e.getMessage(), e);
        }
    }

    @Override
    protected void print(long available, long begin) throws OperationException {
        var beforeSize = Utils.capacityFormat(available);
        var afterSize = Utils.capacityFormat(new File(targetPath).length());
        var end = System.currentTimeMillis();
        var tc = Utils.timeFormat((end - begin) / 1000);
        Tooltips.print(Tooltips.Number._5, tc, beforeSize, afterSize, targetPath);
    }

    @Override
    protected <T> void checkMagicNumber(T stream) throws OperationException {
        var in = (BufferedInputStream) stream;
        var mn = new byte[Constants.MAGIC_NUMBER_BYTES];
        try {
            in.read(mn);
        } catch (Throwable e) {
            throw new OperationException(e.getMessage(), e);
        }
        if (Utils.bytes2Int(mn) != Constants.MAGIC_NUMBER) {
            throw new OperationException("Bad magic number");
        }
    }

    @Override
    protected int getDefaultSize(long available) {
        return available < Constants.DEFAULT_DECRYPT_CONTENT_SIZE ? (int) available : Constants.DEFAULT_DECRYPT_CONTENT_SIZE;
    }

    @Override
    protected <T> void bind(T stream) throws OperationException {
        var in = (BufferedInputStream) stream;
        var temp = new byte[Constants.UUID_BYTES];
        try {
            // 读取hardwareUUID长度
            in.read(temp);
            var length = temp[0] & 0xff;
            if (0 == length) {
                return;
            }
            temp = new byte[length];
            // 读取hardwareUUID
            in.read(temp);
            var uuid = new String(Utils.toBase64Decode(temp), Constants.CHARSET);
            if (!Utils.getUUID().equals(uuid)) {
                throw new OperationException("The UUID does not match,Please decrypt on the same physical device");
            }
            temp = new byte[Constants.FILE_ID_BYTES];
            // 读取fileUUID
            in.read(temp);
            f_uuid = Utils.bytes2Long(temp);
        } catch (Throwable e) {
            throw new OperationException(e.getMessage(), e);
        }
    }

    @Override
    protected void compress(String source, String target) throws OperationException {
        //
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
                var result = decrypt(content);
                out.write(result, 0, result.length);
                out.flush();
                count += len;
                // 输出加密的预计耗时
                if (visibleFlag) {
                    var end = System.currentTimeMillis();
                    Utils.printTimeConsuming(available, (double) (end - begin) / 1000, Constants.DEFAULT_DECRYPT_CONTENT_SIZE);
                    visibleFlag = false;
                }
                // 输出进度条
                Tooltips.printSchedule((double) count / available * 100);
            }
            // 确保最终进度条最终能够追加到100%
            Tooltips.printSchedule(100);
        } catch (Throwable e) {
            throw new OperationException(e.getMessage(), e);
        }
    }

    @Override
    protected String splicTargetFileName(File file) throws NameParseException {
        var dn = file.getName();
        var n = param.getName();
        // 后缀为.dog表示为加密文件
        if (!dn.endsWith(Constants.DEFAULT_SUFFIX)) {
            throw new NameParseException("File suffix must be .dog");
        }
        if (Objects.nonNull(n)) {
            var suffix = Utils.getFileSuffix(Utils.cancelFileSuffix(dn, 1));
            return String.format("%s%s", n, suffix);
        }
        return Utils.cancelFileSuffix(dn, 1);
    }

    /**
     * 数据解密
     *
     * @param data
     * @return
     * @throws DecryptException
     */
    private byte[] decrypt(byte[] data) throws DecryptException {
        return decrypt(data, param.getSecretKey());
    }

    private byte[] decrypt(byte[] data, char[] key) throws DecryptException {
        try {
            var dc = Cipher.getInstance(Constants.DEFAULT_CIPHER_ALGORITHM);
            dc.init(Cipher.DECRYPT_MODE, getSecretKey(key));
            return dc.doFinal(Utils.toBase64Decode(data));
        } catch (Throwable e) {
            throw new DecryptException("The key is incorrect,Try Again", e);
        }
    }
}
