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
package com.github.test.encryptdog.core;

import com.github.encryptdog.core.DataEncrypt;
import com.github.encryptdog.core.DateDecrypt;
import com.github.encryptdog.core.NameParser;
import com.github.encryptdog.exception.DogException;
import com.github.encryptdog.view.ParamDTO;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author gao_xianglong@sina.com
 * @version 0.1-SNAPSHOT
 * @date created in 2022/5/8 22:12
 */
public class DateDecryptTest {
    private static final String FILE_PATH = String.format("%s%s.txt", System.getProperty("java.io.tmpdir"), System.currentTimeMillis());

    @Before
    public void init() {
        try (var out = new PrintWriter(FILE_PATH)) {
            out.write(UUID.randomUUID().toString());
            out.flush();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @After
    public void release() {
        delete(FILE_PATH);
    }

    public boolean delete(String filePath) {
        var file = new File(filePath);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * dog -s source -t target -k
     */
    @Test
    public void testDecrypt_1() throws Throwable {
        // 加密
        ParamDTO param = new ParamDTO();
        param.setTargetPath(System.getProperty("java.io.tmpdir"));
        param.setEncrypt(true);
        param.setDelete(true);
        param.setSecretKey("123456".toCharArray());
        param.setSourceFile(FILE_PATH);
        Assert.assertTrue(new DataEncrypt(param).execute());

        // 解密
        param = new ParamDTO();
        param.setDelete(true);
        param.setTargetPath(System.getProperty("java.io.tmpdir"));
        param.setSecretKey("123456".toCharArray());
        param.setSourceFile(String.format("%s.dog", FILE_PATH));
        Assert.assertTrue(new DateDecrypt(param).execute());
    }

    /**
     * dog -os source -t target -k
     * check pwd
     */
    @Test
    public void testDecrypt_2() throws DogException {
        // 加密
        ParamDTO param = new ParamDTO();
        param.setTargetPath(System.getProperty("java.io.tmpdir"));
        param.setEncrypt(true);
        param.setDelete(true);
        param.setOnlyLocal(true);
        param.setSecretKey("123456".toCharArray());
        param.setSourceFile(FILE_PATH);
        Assert.assertTrue(new DataEncrypt(param).execute());

        // 解密
        param = new ParamDTO();
        param.setTargetPath(System.getProperty("java.io.tmpdir"));
        param.setSecretKey("123457".toCharArray());
        param.setSourceFile(String.format("%s.dog", FILE_PATH));
        var result = false;
        try {
            result = new DateDecrypt(param).execute();
        } catch (DogException e) {
            Assert.assertFalse(result);
        }
        Assert.assertTrue(delete(String.format("%s.dog", FILE_PATH)));
    }

    /**
     * batch
     */
    @Test
    public void testDecrypt_3() throws Throwable {
        // 加密
        var sources = new ArrayList<String>() {{
            for (int i = 0; i < 10; i++) {
                this.add(String.format("%s%s.txt", System.getProperty("java.io.tmpdir"), System.currentTimeMillis()));
                TimeUnit.MILLISECONDS.sleep(100);
            }
        }};
        sources.forEach(x -> {
            try (var out = new PrintWriter(x)) {
                out.write(UUID.randomUUID().toString());
                out.flush();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });
        sources.forEach(x -> {
            Assert.assertTrue(new File(x).exists());
        });
        ParamDTO param = new ParamDTO();
        param.setTargetPath(System.getProperty("java.io.tmpdir"));
        param.setEncrypt(true);
        param.setDelete(true);
        param.setSecretKey("123456".toCharArray());
        var sf = String.format("%s*.txt", System.getProperty("java.io.tmpdir"));
        param.setSourceFile(sf);
        var nameParser = new NameParser();
        Assert.assertTrue(nameParser.parse(param, sf, new DataEncrypt(param), true));
        sources.forEach(x -> {
            Assert.assertTrue(new File(String.format("%s.dog", x)).exists());
        });

        // 解密
        param = new ParamDTO();
        param.setDelete(true);
        param.setTargetPath(System.getProperty("java.io.tmpdir"));
        param.setSecretKey("123456".toCharArray());
        param.setSourceFile(String.format("%s*.dog", System.getProperty("java.io.tmpdir")));
        sf = String.format("%s*.dog", System.getProperty("java.io.tmpdir"));
        nameParser = new NameParser();
        Assert.assertTrue(nameParser.parse(param, sf, new DateDecrypt(param), true));

        sources.forEach(x -> {
            Assert.assertTrue(new File(x).exists());
            Assert.assertTrue(new File(x).delete());
        });
    }
}
