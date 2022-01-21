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

import java.util.Arrays;

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
    /**
     * 秘钥是否转储
     */
    private boolean store;
    /**
     * 是否允许加/解密操作只能在同一物理设备上执行
     */
    private boolean onlyLocal;

    /**
     * Getter method for property <tt>sourceFile</tt>.
     *
     * @return property value of sourceFile
     */
    public String getSourceFile() {
        return sourceFile;
    }

    /**
     * Setter method for property <tt>sourceFile</tt>.
     *
     * @param sourceFile value to be assigned to property sourceFile
     */
    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    /**
     * Getter method for property <tt>targetPath</tt>.
     *
     * @return property value of targetPath
     */
    public String getTargetPath() {
        return targetPath;
    }

    /**
     * Setter method for property <tt>targetPath</tt>.
     *
     * @param targetPath value to be assigned to property targetPath
     */
    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    /**
     * Getter method for property <tt>secretKey</tt>.
     *
     * @return property value of secretKey
     */
    public char[] getSecretKey() {
        return secretKey;
    }

    /**
     * Setter method for property <tt>secretKey</tt>.
     *
     * @param secretKey value to be assigned to property secretKey
     */
    public void setSecretKey(char[] secretKey) {
        this.secretKey = secretKey;
    }

    /**
     * Getter method for property <tt>encrypt</tt>.
     *
     * @return property value of encrypt
     */
    public boolean isEncrypt() {
        return encrypt;
    }

    /**
     * Setter method for property <tt>encrypt</tt>.
     *
     * @param encrypt value to be assigned to property encrypt
     */
    public void setEncrypt(boolean encrypt) {
        this.encrypt = encrypt;
    }

    /**
     * Getter method for property <tt>delete</tt>.
     *
     * @return property value of delete
     */
    public boolean isDelete() {
        return delete;
    }

    /**
     * Setter method for property <tt>delete</tt>.
     *
     * @param delete value to be assigned to property delete
     */
    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    /**
     * Getter method for property <tt>store</tt>.
     *
     * @return property value of store
     */
    public boolean isStore() {
        return store;
    }

    /**
     * Setter method for property <tt>store</tt>.
     *
     * @param store value to be assigned to property store
     */
    public void setStore(boolean store) {
        this.store = store;
    }

    /**
     * Getter method for property <tt>onlyLocal</tt>.
     *
     * @return property value of onlyLocal
     */
    public boolean isOnlyLocal() {
        return onlyLocal;
    }

    /**
     * Setter method for property <tt>onlyLocal</tt>.
     *
     * @param onlyLocal value to be assigned to property onlyLocal
     */
    public void setOnlyLocal(boolean onlyLocal) {
        this.onlyLocal = onlyLocal;
    }

    @Override
    public String toString() {
        return "ParamDTO{" +
                "sourceFile='" + sourceFile + '\'' +
                ", targetPath='" + targetPath + '\'' +
                ", secretKey=" + Arrays.toString(secretKey) +
                ", encrypt=" + encrypt +
                ", delete=" + delete +
                ", store=" + store +
                ", onlyLocal=" + onlyLocal +
                '}';
    }
}