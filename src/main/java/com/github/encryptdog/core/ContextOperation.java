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

    public void start() {
        var fileName = param.getSourceFile();
        var file = new File(fileName);
        var isEncrypt = param.isEncrypt();
        if (file.isFile()) {//判断目标是否是文件
            if (isEncrypt) {//检测加/解密操作
                fileName = String.format("%s%s", file.getName(), Constants.DEFAULT_SUFFIX);//源文件后缀拼接.dog表示为加密文件
            } else {
                if (!file.getName().endsWith(".dog")) {//后缀为.dog表示为加密文件
                    throw new RuntimeException("The target file is not encrypted!!!");
                }
                fileName = file.getName().split(Constants.DEFAULT_SUFFIX)[0];
            }
        } else {
            throw new RuntimeException("Please enter the correct file path!!!");
        }
        try (var in = new BufferedInputStream(new FileInputStream(param.getSourceFile()));
             var out = new BufferedOutputStream(new FileOutputStream(String.format("%s/%s", param.getTargetPath(), fileName)))) {
            var defaultSize = 0;//每次读取的文件内容大小
            var available = in.available();//文件总大小，计算百分比进度条时需要使用
            if (isEncrypt) {
                defaultSize = available < Constants.DEFAULT_ENCRYPT_CONTENT_SIZE ?
                        in.available() : Constants.DEFAULT_ENCRYPT_CONTENT_SIZE;
            } else {
                defaultSize = available < Constants.DEFAULT_DECRYPT_CONTENT_SIZE ?
                        in.available() : Constants.DEFAULT_DECRYPT_CONTENT_SIZE;
            }
            byte[] temp = new byte[defaultSize];
            int len = -1;
            var count = len;
            System.out.println("Please wait...");
            while ((len = in.read(temp)) != -1) {
                var result = isEncrypt ? encrypt(temp).getBytes(Constants.CHARSET) : decrypt(temp);
                out.write(result, 0, result.length);
                out.flush();
                count += len;
                Utils.printSchedule((double) count / available * 100);// 输出进度条
            }
            Utils.printSchedule(100);// 确保最终进度条100%
            System.out.println(String.format("\n%s\toperation success...", isEncrypt ? "Encrypt" : "Decrypt"));
        } catch (Throwable e) {
            e.printStackTrace();
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
    public String encrypt(byte[] data) throws Throwable {
        var ec = Cipher.getInstance(Constants.DEFAULT_CIPHER_ALGORITHM);
        ec.init(Cipher.ENCRYPT_MODE, getSecretKey(param.getSecretKey()));
        return encoder.encodeToString(ec.doFinal(data));//执行数据加密后再base64编码
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
        var dc = Cipher.getInstance(Constants.DEFAULT_CIPHER_ALGORITHM);
        dc.init(Cipher.DECRYPT_MODE, getSecretKey(param.getSecretKey()));
        return dc.doFinal(decoder.decode(data));
    }
}
