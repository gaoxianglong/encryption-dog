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

/**
 * @author gao_xianglong@sina.com
 * @version 1.1-SNAPSHOT
 * @date created in 2021/1/16 10:43 下午
 */
public class ParamDTO {
    /**
     * 需要加/解密的目标文件
     */
    private String sourceFile;
    /**
     * 加/解密内容的转储目录
     */
    private String targetPath;
    /**
     * 秘钥,采用密码选项，不在控制台回显密码
     */
    private char[] secretKey;
    /**
     * true为加密,false为解密
     */
    private boolean encrypt;
    /**
     * 加/解密操作结束后是否删除源文件
     */
    private boolean delete;

    public String getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    public char[] getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(char[] secretKey) {
        this.secretKey = secretKey;
    }

    public boolean isEncrypt() {
        return encrypt;
    }

    public void setEncrypt(boolean encrypt) {
        this.encrypt = encrypt;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    @Override
    public String toString() {
        return "ParamDTO{" +
                "sourceFile='" + sourceFile + '\'' +
                ", targetPath='" + targetPath + '\'' +
                ", secretKey='" + secretKey + '\'' +
                ", encrypt=" + encrypt +
                ", delete=" + delete +
                '}';
    }
}
