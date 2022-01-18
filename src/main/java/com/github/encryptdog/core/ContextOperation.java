/*
 * Copyright 2019-2119 gao_xianglong@sina.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.encryptdog.core;

import com.github.encryptdog.view.ParamVO;
import com.github.utils.Constants;
import com.github.utils.Utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/**
 * 加/解密操作类
 *
 * @author gao_xianglong@sina.com
 * @version 0.1-SNAPSHOT
 * @date created in 2021/1/16 10:47 下午
 */
public class ContextOperation {
    private Base64.Decoder decoder = Base64.getDecoder();
    private Base64.Encoder encoder = Base64.getEncoder();
    private ParamVO param;

    public ContextOperation(ParamVO param) {
        this.param = param;
    }

    public void start() throws Throwable {
        var fileName = param.getSourceFile();
        var file = new File(fileName);
        var isEncrypt = param.isEncrypt();
        if (!file.exists()) {
            throw new Exception(String.format("file %s does not exist", fileName));
        }
        if (file.isFile()) {
            // 检测加/解密操作
            if (isEncrypt) {
                // 源文件后缀拼接.dog表示为加密文件
                fileName = String.format("%s%s", file.getName(), Constants.DEFAULT_SUFFIX);
            } else {
                // 后缀为.dog表示为加密文件
                if (!file.getName().endsWith(Constants.DEFAULT_SUFFIX)) {
                    throw new Exception("File suffix must be .dog");
                }
                fileName = file.getName().substring(0, file.getName().length() - Constants.DEFAULT_SUFFIX.length());
            }
        } else {
            throw new Exception("Please enter the correct file path");
        }
        var begin = System.currentTimeMillis();
        var targetPath = String.format("%s/%s", param.getTargetPath(), fileName);
        try (var in = new BufferedInputStream(new FileInputStream(param.getSourceFile()));
             var out = new BufferedOutputStream(new FileOutputStream(targetPath))) {
            // 每次读取的文件内容大小
            int defaultSize = 0;
            // 文件总大小，计算百分比进度条时需要使用
            var available = file.length();
            if (available < 1) {
                throw new Exception("There is nothing in the target file");
            }
            if (isEncrypt) {
                defaultSize = available < Constants.DEFAULT_ENCRYPT_CONTENT_SIZE ?
                        (int) available : Constants.DEFAULT_ENCRYPT_CONTENT_SIZE;
            } else {
                defaultSize = available < Constants.DEFAULT_DECRYPT_CONTENT_SIZE ?
                        (int) available : Constants.DEFAULT_DECRYPT_CONTENT_SIZE;
            }
            var content = new byte[defaultSize];
            var len = -1;
            var count = len;
            System.out.println("Please wait...");
            // 魔术检测
            if (isEncrypt) {
                byte[] mn = Utils.int2Bytes(Constants.MAGIC_NUMBER);
                // 文件起始位写入4bytes魔术
                out.write(mn, 0, mn.length);
                out.flush();
            } else {
                var mn = new byte[Constants.MAGIC_NUMBER_SIZE];
                in.read(mn);
                if (Utils.bytes2Int(mn) != Constants.MAGIC_NUMBER) {
                    throw new Exception("The target file is not a dog file,magic number:0x19890225");
                }
            }
            while ((len = in.read(content)) != -1) {
                if (len < defaultSize) {
                    var temp = new byte[len];
                    System.arraycopy(content, 0, temp, 0, len);
                    content = temp;
                }
                var result = isEncrypt ? encrypt(content) : decrypt(content);
                out.write(result, 0, result.length);
                out.flush();
                count += len;
                Utils.printSchedule((double) count / available * 100);// 输出进度条
            }
            Utils.printSchedule(100);// 确保最终进度条100%
            var end = System.currentTimeMillis();
            var tc = (double) (end - begin) / 1000;
            System.out.println(String.format("\n\n%s\tsuccess\n[Time-consuming]:%.2f%s,[Before size]:%.2fMB,[After size]:%.2fMB\n[Target path]:%s", isEncrypt ? "Encrypt" : "Decrypt",
                    tc, tc >= 1 ? "s" : "ms", (double) available / 0X100000, (double) new File(targetPath).length() / 0X100000, targetPath));
        } finally {
            Arrays.fill(param.getSecretKey(), ' ');// 清空数组
            if (param.isDelete()) {//操作结束后是否删除源文件
                new File(param.getSourceFile()).delete();
            }
        }
    }

    /**
     * 数据加密
     *
     * @param data
     * @return
     * @throws Throwable
     */
    public byte[] encrypt(byte[] data) throws Throwable {
        var ec = Cipher.getInstance(Constants.DEFAULT_CIPHER_ALGORITHM);
        ec.init(Cipher.ENCRYPT_MODE, getSecretKey(param.getSecretKey()));
        return encoder.encodeToString(ec.doFinal(data)).getBytes(Constants.CHARSET);//执行数据加密后再base64编码
    }

    /**
     * 返回秘钥器
     *
     * @param key
     * @return
     * @throws Throwable
     */
    private SecretKeySpec getSecretKey(char[] key) throws Throwable {
        var random = SecureRandom.getInstance(Constants.ALGORITHM);
//        random.setSeed(key.getBytes(Constants.CHARSET));
        random.setSeed(Utils.toBytes(key));
        var kg = KeyGenerator.getInstance(Constants.KEY_ALGORITHM);//获取秘钥生成器
        kg.init(random);
        var generateKey = kg.generateKey();//生成秘钥
        return new SecretKeySpec(generateKey.getEncoded(), Constants.KEY_ALGORITHM);
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
