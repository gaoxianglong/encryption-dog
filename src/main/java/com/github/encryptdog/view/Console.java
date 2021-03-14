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

import com.github.encryptdog.core.ContextOperation;
import picocli.CommandLine;

import java.util.Objects;

/**
 * 控制台程序
 *
 * @author gao_xianglong@sina.com
 * @version 0.1-SNAPSHOT
 * @date created in 2021/1/16 4:53 下午
 */
@CommandLine.Command(name = "encrypt-dog", footer = "Copyright(c) 2021", version = "1.0-SNAPSHOT", mixinStandardHelpOptions = true)
public class Console implements Runnable {
    /**
     * 需要加/解密的目标文件
     */
    @CommandLine.Option(names = {"-s", "--source-file"}, paramLabel = "<source file>", required = true, description = "Target files that need to be encrypt and decrypt")
    private String sourceFile;

    /**
     * 加/解密内容的转储目录
     */
    @CommandLine.Option(names = {"-t", "--target-path"}, paramLabel = "<storage path>", required = true, description = "Storage path after operation")
    private String targetPath;

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
        if (Objects.isNull(secretKey) || secretKey.length <= 0) {
            throw new RuntimeException("Password cannot be empty");
        }
        if (Objects.isNull(sourceFile) || sourceFile.isBlank()) {
            throw new RuntimeException("SourceFile cannot be empty");
        }
        if (Objects.isNull(targetPath) || targetPath.isBlank()) {
            throw new RuntimeException("TargetPath cannot be empty");
        }
        var param = new ParamVO();
        param.setSourceFile(sourceFile);
        param.setTargetPath(targetPath);
        param.setDelete(delete);
        param.setEncrypt(encrypt);
        param.setSecretKey(secretKey);
        new ContextOperation(param).start();
    }
}
