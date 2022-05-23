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
package com.github.utils;

import com.github.encryptdog.exception.OperationException;

import java.io.*;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author gao_xianglong@sina.com
 * @version 1.1-SNAPSHOT
 * @date created in 2021/1/17 12:05 上午
 */
public class Utils {
    /**
     * base64加密
     *
     * @param s
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String toBase64Encode(byte[] s) throws UnsupportedEncodingException {
        return Base64.getEncoder().encodeToString(s);
    }

    /**
     * base64解密
     *
     * @param s
     * @return
     */
    public static byte[] toBase64Decode(byte[] s) {
        return Base64.getDecoder().decode(s);
    }

    /**
     * 字符转字节
     *
     * @param chars
     * @return
     */
    public static byte[] toBytes(char[] chars) {
        var cs = Charset.forName(Constants.CHARSET);
        var cb = CharBuffer.allocate(chars.length);
        cb.put(chars);
        cb.flip();
        var bb = cs.encode(cb);
        return bb.array();
    }

    /**
     * 字节转字符
     *
     * @param bytes
     * @return
     */
    public static char[] toChars(byte[] bytes) {
        var cs = Charset.forName(Constants.CHARSET);
        var bb = ByteBuffer.allocate(bytes.length);
        bb.put(bytes).flip();
        var cb = cs.decode(bb);
        return cb.array();
    }

    /**
     * 整型转字节数组
     *
     * @param n
     * @return
     */
    public static byte[] int2Bytes(int n) {
        var result = new byte[4];
        result[3] = (byte) (n & 0xff);
        result[2] = (byte) (n >> 8 & 0xff);
        result[1] = (byte) (n >> 16 & 0xff);
        result[0] = (byte) (n >> 24 & 0xff);
        return result;
    }

    /**
     * 字节数组转整型
     *
     * @param b
     * @return
     */
    public static int bytes2Int(byte[] b) {
        Objects.requireNonNull(b);
        var result = 0;
        for (var i = 0; i < b.length; i++) {
            result += (b[i] & 0xff) << ((3 - i) * 8);
        }
        return result;
    }

    /**
     * 输出预计耗时
     *
     * @param available
     * @param tc
     * @param cs
     */
    public static void printTimeConsuming(long available, double tc, int cs) {
        var tca = available / cs;
        var result = ((tca < 1 ? 1 : tca) * tc);
        //System.out.println(String.format("[Estimated time]:%.2f%s", result, result > 1 ? "s" : "ms"));
        System.out.println(String.format("[Estimated time]:%s", Utils.timeFormat((long) result)));
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(System.currentTimeMillis());
    }

    /**
     * 输出所有异常提示信息
     *
     * @param msg
     */
    public static void printErrMsg(String msg, boolean encrypt) {
        for (var i = 0; i < msg.length(); i++) {
            System.out.print("-");
        }
        System.out.println(String.format("\n[%s result]:failed", encrypt ? "Encryption" : "Decryption"));
        System.out.println(String.format("%s", msg));
        for (var i = 0; i < msg.length(); i++) {
            System.out.print("-");
        }
        System.out.println("\n");
    }

    /**
     * 获取设备唯一标识,如果是mac取Hardware UUID,如果是win或linux则取MAC物理地址
     *
     * @return
     * @throws OperationException
     */
    public static String getUUID() throws OperationException {
        String uuid = null;
        var os = getOSName();
        try {
//            // 优先取HardwareUUID,失败则去MAC ID
//            uuid = getHardwareUUID();
//            if (Objects.isNull(uuid)) {
//                uuid = getMacAddress();
//            }
            // --only-local命令仅支持mac
            uuid = getHardwareUUID();
        } catch (Throwable e) {
            throw new OperationException("Failed to get hardware-uuid", e);
        }
        return uuid;
    }

    /**
     * 操作系统验证
     *
     * @return
     */
    public static boolean isMac() {
        return Utils.getOSName().toLowerCase().startsWith("mac");
    }

    /**
     * 获取操作系统名称
     *
     * @return
     */
    public static String getOSName() {
        return System.getProperty("os.name");
    }

    /**
     * 借助system_profiler命令获取Hardware UUID
     *
     * @return
     * @throws IOException
     * @throws OperationException
     */
    private static String getHardwareUUID() throws Throwable {
        var process = Runtime.getRuntime().exec(Constants.UUID_COMMAND);
        try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line = null;
            while (Objects.nonNull(line = reader.readLine())) {
                line = line.trim();
                if (line.indexOf("Hardware UUID") != -1) {
                    return line.substring(line.indexOf(":") + 1).trim();
                }
            }
        }
        return null;
    }

    /**
     * 获取MAC物理地址
     *
     * @return
     * @throws Throwable
     */
    private static String getMacAddress() throws Throwable {
        var en = NetworkInterface.getNetworkInterfaces();
        final var builder = new StringBuilder();
        var list = new ArrayList<>();
        while (en.hasMoreElements()) {
            var iface = en.nextElement();
            var addrs = iface.getInterfaceAddresses();
            for (var addr : addrs) {
                var ip = addr.getAddress();
                var network = NetworkInterface.getByInetAddress(ip);
                if (Objects.isNull(network)) {
                    continue;
                }
                var mac = network.getHardwareAddress();
                if (Objects.isNull(mac)) {
                    continue;
                }
                builder.delete(0, builder.length());
                for (var i = 0; i < mac.length; i++) {
                    builder.append(String.format("%02X", mac[i]));
                }
                list.add(builder.toString());
            }
        }
        builder.delete(0, builder.length());
        var unique = list.stream().distinct().collect(Collectors.toList());
        unique.forEach(x -> builder.append(String.format("%s-", x)));
        return String.format("M:%s", builder.toString().substring(0, builder.length() - 1));
    }

    /**
     * 将耗时(单位s)换为指定的时间格式 hh:mm:ss
     *
     * @param time
     * @return
     */
    public static String timeFormat(long time) {
        var result = new StringBuilder();
        var hour = time / Constants.HOUR;
        time -= hour * Constants.HOUR;
        var min = time / Constants.MINUTE;
        time -= min * Constants.MINUTE;
        result.append(String.format("%02d", hour)).append(":").
                append(String.format("%02d", min)).append(":").
                append(String.format("%02d", time));
        return result.toString();
    }

    /**
     * 容量单位转换
     *
     * @param size
     * @return
     */
    public static String capacityFormat(long size) {
        if (size >= Constants._1GB) {
            return String.format("%.2fGB", (double) size / Constants._1GB);
        }
        return String.format("%.2fMB", (double) size / Constants._1MB);
    }

    /**
     * 裁剪文件后缀
     *
     * @param fn
     * @param n
     * @return
     */
    public static String cancelFileSuffix(String fn, int n) {
        if (n < 1) {
            return fn;
        }
        return cancelFileSuffix(fn.substring(0, fn.lastIndexOf(".")), --n);
    }

    /**
     * 删除指定文件
     *
     * @param fp
     * @return
     */
    public static boolean deleteFile(String fp) {
        var result = false;
        var file = new File(fp);
        if (file.exists()) {
            result = file.delete();
        }
        return result;
    }

    /**
     * 获取文件后缀
     *
     * @param fn
     * @return
     */
    public static String getFileSuffix(String fn) {
        return fn.substring(fn.lastIndexOf("."));
    }

    /**
     * long转字节数组
     *
     * @param x
     * @return
     */
    public static byte[] long2Bytes(long x) {
        var buffer = ByteBuffer.allocate(8);
        buffer.putLong(0, x);
        return buffer.array();
    }

    /**
     * 字节数组转long
     *
     * @param bytes
     * @return
     */
    public static long bytes2Long(byte[] bytes) {
        var buffer = ByteBuffer.allocate(8);
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();
        return buffer.getLong();
    }
}