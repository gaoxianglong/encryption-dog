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
import com.github.utils.Constants;
import picocli.CommandLine;

import java.util.Arrays;
import java.util.Objects;

/**
 * 控制台程序
 *
 * @author gao_xianglong@sina.com
 * @version 1.1-SNAPSHOT
 * @date created in 2021/1/16 4:53 下午
 */
@CommandLine.Command(name = "encrypt-dog", footer = "Copyright(c) 2021-2031", version = "1.3-SNAPSHOT", mixinStandardHelpOptions = true)
public class Console implements Runnable {
    /**
     * 需要加/解密的目标文件
     */
    @CommandLine.Option(names = {"-s", "--source-file"}, paramLabel = "<source file>", required = true, description = "Target files that need to be encrypt and decrypt")
    private String sourceFile;

    /**
     * 加/解密内容的转储目录,非必填选项，缺省存储在临时目录下
     */
    @CommandLine.Option(names = {"-t", "--target-path"}, paramLabel = "<storage path>", description = "Storage path after operation,The default is stored in the temporary directory")
    private String targetPath = Constants.DEFAULT_TARGET_PATH;

    /**
     * 秘钥,采用密码选项，不在控制台回显密码
     */
    @CommandLine.Option(names = {"-k", "--secret-key"}, paramLabel = "<secret key>", required = true, description = "Both encrypt and decrypt require the same secret key", interactive = true)
    private char[] secretKey;

    /**
     * true为加密,false为解密
     */
    @CommandLine.Option(names = {"-e", "--encrypt"}, description = "The default is decryption mode")
    private boolean encrypt;

    /**
     * 加/解密操作结束后是否删除源文件
     */
    @CommandLine.Option(names = {"-d", "--delete"}, description = "The source file is not deleted after the default operation")
    private boolean delete;

    @Override
    public void run() {
        try {
            if (Objects.isNull(secretKey) || secretKey.length <= 0) {
                throw new Exception("Secret-key cannot be empty");
            }
            if (Objects.isNull(sourceFile) || sourceFile.isBlank()) {
                throw new Exception("Source file cannot be empty");
            }
            var param = new ParamDTO();
            param.setSourceFile(sourceFile);
            param.setTargetPath(targetPath);
            param.setDelete(delete);
            param.setEncrypt(encrypt);
            param.setSecretKey(secretKey);
            //checkSecretKey();
            (encrypt ? new DataEncrypt(param) : new DateDecrypt(param)).execute();
        } catch (Throwable t) {
            printErrMsg(t.getMessage());
        }
    }

    /**
     * 由于秘钥不回显,加密操作时为了防止输错，所以需要重复一次
     *
     * @throws Throwable
     */
    private void checkSecretKey() throws Throwable {
        if (!encrypt) {
            return;
        }
        java.io.Console console = System.console();
        if (Objects.isNull(console)) {
            throw new Exception("Couldn't get Console instance, maybe you're running this from within an IDE?");
        }
        var newSecretKey = console.readPassword("Enter the secret-key again: ");
        if (Objects.isNull(newSecretKey) || !(Objects.equals(new String(secretKey), new String(newSecretKey)))) {
            throw new Exception("The two secret-key do not match");
        }
    }

    /**
     * 输出所有异常提示信息
     *
     * @param msg
     */
    private void printErrMsg(String msg) {
        for (var i = 0; i < msg.length(); i++) {
            System.out.print("-");
        }
        System.out.println(String.format("\n%s failed", encrypt ? "Encryption" : "Decryption"));
        System.out.println(String.format("%s", msg));
        for (var i = 0; i < msg.length(); i++) {
            System.out.print("-");
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
                '}';
    }
}
