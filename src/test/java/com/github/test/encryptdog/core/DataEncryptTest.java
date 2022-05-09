package com.github.test.encryptdog.core;/*
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

import com.github.encryptdog.core.DataEncrypt;
import com.github.encryptdog.core.NameParser;
import com.github.encryptdog.view.ParamDTO;
import com.github.utils.Constants;
import com.github.utils.Utils;
import org.junit.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author gao_xianglong@sina.com
 * @version 0.1-SNAPSHOT
 * @date created in 2022/5/8 14:28
 */
public class DataEncryptTest {
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
     * dog -es source -t target -k
     */
    @Test
    public void testEncrypt_1() throws Throwable {
        ParamDTO param = new ParamDTO();
        param.setTargetPath(System.getProperty("java.io.tmpdir"));
        param.setEncrypt(true);
        param.setSecretKey("123456".toCharArray());
        param.setSourceFile(FILE_PATH);
        Assert.assertTrue(new DataEncrypt(param).execute());
        Assert.assertTrue(delete(String.format("%s.dog", FILE_PATH)));
    }

    /**
     * dog -des source -t target -k
     */
    @Test
    public void testEncrypt_2() throws Throwable {
        ParamDTO param = new ParamDTO();
        param.setDelete(true);
        param.setTargetPath(System.getProperty("java.io.tmpdir"));
        param.setEncrypt(true);
        param.setSecretKey("123456".toCharArray());
        param.setSourceFile(FILE_PATH);
        Assert.assertTrue(new File(FILE_PATH).exists());
        Assert.assertTrue(new DataEncrypt(param).execute());
        Assert.assertFalse(new File(FILE_PATH).exists());
        Assert.assertTrue(delete(String.format("%s.dog", FILE_PATH)));
    }

    /**
     * dog -odes source -t target -k
     * check only-local
     */
    @Test
    public void testEncrypt_3() throws Throwable {
        ParamDTO param = new ParamDTO();
        param.setDelete(true);
        param.setOnlyLocal(true);
        param.setTargetPath(System.getProperty("java.io.tmpdir"));
        param.setEncrypt(true);
        param.setSecretKey("123456".toCharArray());
        param.setSourceFile(FILE_PATH);
        Assert.assertTrue(new File(FILE_PATH).exists());
        Assert.assertTrue(new DataEncrypt(param).execute());
        Assert.assertFalse(new File(FILE_PATH).exists());
        try (BufferedInputStream in = new BufferedInputStream(
                new FileInputStream(String.format(String.format("%s.dog", FILE_PATH))))) {
            var temp = new byte[Constants.MAGIC_NUMBER_SIZE];
            in.read(temp);
            var uuid = Utils.toBase64Encode(Utils.getUUID().getBytes(Constants.CHARSET)).getBytes(Constants.CHARSET);
            temp = new byte[Constants.UUID_FLAG_SIZE];
            in.read(temp);
            var size = Utils.bytes2Int(temp);
            Assert.assertEquals(size, uuid.length);
            temp = new byte[size];
            in.read(temp);
            Assert.assertEquals(new String(Utils.toBase64Decode(uuid), Constants.CHARSET),
                    new String(Utils.toBase64Decode(temp), Constants.CHARSET));
        }
        Assert.assertTrue(delete(String.format("%s.dog", FILE_PATH)));
    }

    /**
     * check only-local
     */
    @Test
    public void testEncrypt_4() throws Throwable {
        ParamDTO param = new ParamDTO();
        param.setTargetPath(System.getProperty("java.io.tmpdir"));
        param.setEncrypt(true);
        param.setSecretKey("123456".toCharArray());
        param.setSourceFile(FILE_PATH);
        Assert.assertTrue(new DataEncrypt(param).execute());
        try (BufferedInputStream in = new BufferedInputStream(
                new FileInputStream(String.format(String.format("%s.dog", FILE_PATH))))) {
            var temp = new byte[Constants.MAGIC_NUMBER_SIZE];
            in.read(temp);
            temp = new byte[Constants.UUID_FLAG_SIZE];
            in.read(temp);
            var size = Utils.bytes2Int(temp);
            Assert.assertEquals(size, 0);
        }
        Assert.assertTrue(delete(String.format("%s.dog", FILE_PATH)));
    }

    /**
     * check only-local
     */
    @Test
    public void testEncrypt_5() throws Throwable {
        var param = new ParamDTO();
        param.setTargetPath(System.getProperty("java.io.tmpdir"));
        param.setEncrypt(true);
        param.setOnlyLocal(true);
        param.setSecretKey("123456".toCharArray());
        param.setSourceFile(FILE_PATH);
        Assert.assertTrue(new DataEncrypt(param).execute());
        try (BufferedInputStream in = new BufferedInputStream(
                new FileInputStream(String.format(String.format("%s.dog", FILE_PATH))))) {
            var temp = new byte[Constants.MAGIC_NUMBER_SIZE];
            in.read(temp);
            temp = new byte[Constants.UUID_FLAG_SIZE];
            in.read(temp);
            var size = Utils.bytes2Int(temp);
            temp = new byte[size];
            in.read(temp);
            var uuid = Utils.toBase64Encode(UUID.randomUUID().toString().getBytes(Constants.CHARSET)).getBytes(Constants.CHARSET);
            Assert.assertNotEquals(new String(Utils.toBase64Decode(uuid), Constants.CHARSET),
                    new String(Utils.toBase64Decode(temp), Constants.CHARSET));
        }
        Assert.assertTrue(delete(String.format("%s.dog", FILE_PATH)));
    }

    /**
     * check only-local
     */
    @Test
    public void testEncrypt_6() throws Throwable {
        var param = new ParamDTO();
        param.setTargetPath(System.getProperty("java.io.tmpdir"));
        param.setEncrypt(true);
        param.setOnlyLocal(true);
        param.setSecretKey("123456".toCharArray());
        param.setSourceFile(FILE_PATH);
        Assert.assertTrue(new DataEncrypt(param).execute());

        var temp = String.format("%s.dog", param.getSourceFile());
        try (var in = new BufferedInputStream(new FileInputStream(Constants.STORE_PWD_FILE_PATH))) {
            var properties = new Properties();
            properties.load(in);
            var file = new File(temp);
            var sf = properties.get(String.format("%s-%s", file.getName(), file.length()));
            Assert.assertNotNull(sf);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        Assert.assertTrue(delete(String.format("%s.dog", FILE_PATH)));

        param.setOnlyLocal(false);
        Assert.assertTrue(new DataEncrypt(param).execute());
        temp = String.format("%s.dog", param.getSourceFile());
        try (var in = new BufferedInputStream(new FileInputStream(Constants.STORE_PWD_FILE_PATH))) {
            var properties = new Properties();
            properties.load(in);
            var file = new File(temp);
            var sf = properties.get(String.format("%s-%s", file.getName(), file.length()));
            Assert.assertNull(sf);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        Assert.assertTrue(delete(String.format("%s.dog", FILE_PATH)));

        param.setOnlyLocal(true);
        Assert.assertTrue(new DataEncrypt(param).execute());
        temp = String.format("%s.dog", param.getSourceFile());
        try (var in = new BufferedInputStream(new FileInputStream(Constants.STORE_PWD_FILE_PATH))) {
            var properties = new Properties();
            properties.load(in);
            var file = new File(temp);
            var sf = properties.get(String.format("%s-%s", file.getName(), file.length()));
            Assert.assertNotNull(sf);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        Assert.assertTrue(delete(String.format("%s.dog", FILE_PATH)));
    }

    /**
     * default target
     */
    @Test
    public void testEncrypt_7() throws Throwable {
        ParamDTO param = new ParamDTO();
        // 使用缺省tp
        param.setTargetPath(Constants.DEFAULT_USER_DESKTOP_PATH);
        param.setSourceFile(FILE_PATH);
        param.setEncrypt(true);
        param.setDelete(true);
        param.setSecretKey("123456".toCharArray());
        // 获取源文件名
        var fn = FILE_PATH.substring(FILE_PATH.lastIndexOf(File.separator));
        Assert.assertTrue(new DataEncrypt(param).execute());
        System.out.println(String.format("%s%s.dog", Constants.DEFAULT_USER_DESKTOP_PATH, fn));
        Assert.assertTrue(delete(String.format("%s%s.dog", Constants.DEFAULT_USER_DESKTOP_PATH, fn)));
    }

    /**
     * dog -ces source -t target -k
     */
    @Test
    public void testEncrypt_8() throws Throwable {
        ParamDTO param = new ParamDTO();
        param.setCompress(true);
        param.setDelete(true);
        param.setTargetPath(System.getProperty("java.io.tmpdir"));
        param.setEncrypt(true);
        param.setSecretKey("123456".toCharArray());
        param.setSourceFile(FILE_PATH);
        Assert.assertTrue(new DataEncrypt(param).execute());
        Assert.assertTrue(delete(String.format("%s.zip", FILE_PATH.substring(0, FILE_PATH.lastIndexOf(".")))));
    }

    /**
     * batch
     */
    @Test
    public void testEncrypt_9() throws Throwable {
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
            Assert.assertTrue(delete(String.format("%s.dog", x)));
        });
        sources.forEach(x -> {
            Assert.assertFalse(new File(x).exists());
        });
        Assert.assertTrue(delete(String.format("%s.dog", FILE_PATH)));
    }

    /**
     * dog -es source -t target -n name -k
     */
    @Test
    public void testEncrypt_10() throws Throwable {
        ParamDTO param = new ParamDTO();
        param.setDelete(true);
        param.setTargetPath(System.getProperty("java.io.tmpdir"));
        param.setEncrypt(true);
        param.setSecretKey("123456".toCharArray());
        param.setSourceFile(FILE_PATH);
        param.setName("test");
        Assert.assertTrue(new DataEncrypt(param).execute());
        Assert.assertTrue(new File(String.format("%stest.txt.dog", System.getProperty("java.io.tmpdir"))).exists());
        Assert.assertTrue(new File(String.format("%stest.txt.dog", System.getProperty("java.io.tmpdir"))).delete());
    }

    /**
     * dog -es source -t target -k
     * check magic
     */
    @Test
    public void testCheckMagicNumber() throws Throwable {
        ParamDTO param = new ParamDTO();
        param.setTargetPath(System.getProperty("java.io.tmpdir"));
        param.setEncrypt(true);
        param.setSecretKey("123456".toCharArray());
        param.setSourceFile(FILE_PATH);
        Assert.assertTrue(new DataEncrypt(param).execute());
        try (BufferedInputStream in = new BufferedInputStream(
                new FileInputStream(String.format(String.format("%s.dog", FILE_PATH))))) {
            var temp = new byte[Constants.MAGIC_NUMBER_SIZE];
            var len = in.read(temp);
            Assert.assertEquals(Constants.MAGIC_NUMBER_SIZE, len);
            Assert.assertEquals(Utils.bytes2Int(temp), Constants.MAGIC_NUMBER);
        }
        Assert.assertTrue(delete(String.format("%s.dog", FILE_PATH)));
    }
}