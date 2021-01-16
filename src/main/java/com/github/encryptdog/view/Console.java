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
    @CommandLine.Option(names = {"-s", "--source-file"}, paramLabel = "<源文件地址>", required = true, description = "需要加/解密的目标文件")
    private String sourceFile;

    /**
     * 加/解密内容的转储目录
     */
    @CommandLine.Option(names = {"-t", "--target-path"}, paramLabel = "<转储路径>", required = true, description = "加/解密内容的转储目录")
    private String targetPath;

    /**
     * 秘钥
     */
    @CommandLine.Option(names = {"-k", "--secret-key"}, paramLabel = "<秘钥>", required = true, description = "加/解密都需要使用同一秘钥")
    private String secretKey;

    /**
     * true为加密,false为解密
     */
    @CommandLine.Option(names = {"-e", "--encrypt"}, description = "缺省为解密模式")
    private boolean encrypt;

    /**
     * 加/解密操作结束后是否删除源文件
     */
    @CommandLine.Option(names = {"-d", "--delete"}, description = "缺省操作结束后不删除源文件")
    private boolean delete;

    @Override
    public void run() {
        var param = new ParamVO();
        param.setSourceFile(sourceFile);
        param.setTargetPath(targetPath);
        param.setDelete(delete);
        param.setEncrypt(encrypt);
        param.setSecretKey(secretKey);
        new ContextOperation(param).start();
    }
}
