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
package com.github.encryptdog.view;

import com.github.encryptdog.core.DataEncrypt;
import com.github.encryptdog.core.DateDecrypt;
import com.github.encryptdog.core.DelSource;
import com.github.encryptdog.core.NameParser;
import com.github.encryptdog.exception.DogException;
import com.github.encryptdog.exception.OperationException;
import com.github.utils.Constants;
import com.github.utils.Utils;
import picocli.CommandLine;

import java.util.*;

/**
 * 控制台程序
 *
 * @author gao_xianglong@sina.com
 * @version 1.1-SNAPSHOT
 * @date created in 2021/1/16 4:53 下午
 */
@CommandLine.Command(name = "encrypt-dog", footer = "Copyright(c) 2021-2031", version = Constants.VERSION, mixinStandardHelpOptions = true)
public class Console implements Runnable {
    /**
     * 需要加/解密的目标文件
     */
    @CommandLine.Option(names = {"-s", "--source-file"}, paramLabel = "<source file>", required = true, description = "Target files that need to be encrypt and decrypt,Wildcards are supported.")
    private String sourceFile;

    /**
     * 加/解密内容的转储目录,非必填选项，缺省存储在桌面
     */
    @CommandLine.Option(names = {"-t", "--target-path"}, paramLabel = "<storage path>", description = "Storage path after operation,The default is stored in the user home directory.")
    private String targetPath = Constants.DEFAULT_USER_DESKTOP_PATH;

    /**
     * 秘钥,采用密码选项，不在控制台回显密码
     */
    @CommandLine.Option(names = {"-k", "--secret-key"}, paramLabel = "<secret key>", required = true, description = "Both encrypt and decrypt require the same secret key", interactive = true)
    private char[] secretKey;

    /**
     * true为加密,false为解密
     */
    @CommandLine.Option(names = {"-e", "--encrypt"}, description = "The default is decryption mode.")
    private boolean encrypt;

    /**
     * 加/解密操作结束后是否删除源文件
     */
    @CommandLine.Option(names = {"-d", "--delete"}, description = "The source file is not deleted after the default operation.")
    private boolean delete;

    /**
     * 仅限加/解密操作在同一台物理设备上,提升安全系数
     */
    @CommandLine.Option(names = {"-o", "--only-local"}, description = "Encryption and decryption operations can only be performed on the same physical device.")
    private boolean onlyLocal;

    /**
     * 是否在加密操作完成后执行压缩
     */
    @CommandLine.Option(names = {"-c", "--compress"}, description = "Compression is not enabled by default,Turning on compression will increase execution time.")
    private boolean compress;

    @Override
    public void run() {
        try {
            if (Objects.isNull(secretKey) || secretKey.length <= 0) {
                throw new OperationException("Secret-key cannot be empty");
            }
            if (Objects.isNull(sourceFile) || sourceFile.isBlank()) {
                throw new OperationException("Source file cannot be empty");
            }
            // double check pwd
            checkSecretKey();
            ParamDTO param = new ParamDTO();
            param.setTargetPath(targetPath);
            param.setDelete(new DelSource().deleteConfirmation(delete));
            param.setEncrypt(encrypt);
            param.setSecretKey(secretKey);
            param.setStore(Boolean.parseBoolean(System.getProperty(Constants.STORE)));
            param.setOnlyLocal(onlyLocal);
            param.setCompress(compress);
            var aot = encrypt ? new DataEncrypt(param) : new DateDecrypt(param);
            new NameParser().parse(param, sourceFile, aot);
        } catch (DogException t) {
            Utils.printErrMsg(t.getMessage(), encrypt);
        }
    }

    /**
     * 由于秘钥不回显,加密操作时为了防止输错，所以需要重复一次
     *
     * @throws OperationException
     */
    private void checkSecretKey() throws OperationException {
        if (!encrypt) {
            return;
        }
        java.io.Console console = System.console();
        if (Objects.isNull(console)) {
            throw new OperationException("Couldn't get Console instance, maybe you're running this from within an IDE?");
        }
        var newSecretKey = console.readPassword("Enter the secret-key again: ");
        if (Objects.isNull(newSecretKey) || !(Objects.equals(new String(secretKey), new String(newSecretKey)))) {
            throw new OperationException("The two secret-key do not match");
        }
    }

    @Override
    public String toString() {
        return "Console{" +
                "sourceFile='" + sourceFile + '\'' +
                ", targetPath='" + targetPath + '\'' +
                ", secretKey=" + Arrays.toString(secretKey) +
                ", encrypt=" + encrypt +
                ", delete=" + delete +
                ", onlyLocal=" + onlyLocal +
                ", compress=" + compress +
                '}';
    }
}