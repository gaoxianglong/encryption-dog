/*
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.github.encryptdog.core;

import com.github.encryptdog.exception.DogException;
import com.github.encryptdog.exception.NameParseException;
import com.github.encryptdog.view.ParamDTO;
import com.github.encryptdog.view.Tooltips;
import com.github.utils.Constants;
import com.github.utils.Utils;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 文件名解析器
 *
 * @author jiushu
 * @version NameParser.java, v 0.1 2022年01月21日 11:19 上午 jiushu
 */
public class NameParser {
    /**
     * 执行解析
     *
     * @param param
     * @param sourceFile
     * @param aot
     * @throws NameParseException
     */
    public void parse(ParamDTO param, String sourceFile, AbstractOperationTemplate aot) throws NameParseException {
        var sfs = parseSourceFileName(sourceFile);
        operationConfirmation(sfs);
        Tooltips.print(Tooltips.Number._3);
        final var i = new AtomicInteger();
        final var fc = new AtomicInteger();
        var size = sfs.size();
        var begin = System.currentTimeMillis();
        try {
            sfs.forEach(sf -> {
                try {
                    Tooltips.print(Tooltips.Number._4, param.isEncrypt(), i.incrementAndGet(), size, sf);
                    param.setSourceFile(sf);
                    aot.execute();
                } catch (DogException t) {
                    fc.incrementAndGet();
                    Utils.printErrMsg(t.getMessage(), param.isEncrypt());
                }
            });
        } finally {
            release(param);
        }
        var tc = Utils.timeFormat((System.currentTimeMillis() - begin) / 1000);
        Tooltips.print(Tooltips.Number._6, tc, size, fc.get());
        if (param.isStore()) {
            Tooltips.print(Tooltips.Number._7);
        }
    }

    /**
     * 使用通配符时,需要进行源文件确认
     *
     * @param sfs
     */
    private void operationConfirmation(List<String> sfs) {
        if (sfs.size() < 2) {
            return;
        }
        Tooltips.print(Tooltips.Number._1);
        var i = 0;
        for (var sf : sfs) {
            System.out.println(String.format("%s.%s", ++i, sf));
        }
        Tooltips.print(Tooltips.Number._2);
        var scanner = new Scanner(System.in);
        var line = scanner.nextLine();
        if (!"Y".equalsIgnoreCase(line)) {
            System.out.println("Bye~");
            System.exit(0);
        }
    }

    /**
     * 解析源文件名称
     *
     * @param sf
     * @return
     * @throws NameParseException
     */
    private List<String> parseSourceFileName(String sf) throws NameParseException {
        var result = new ArrayList<String>();
        var nsf = duplicateRemoval(sf);
        if (Objects.nonNull(nsf)) {
            var t1 = nsf.split(Constants.SEPARATOR);
            var t2 = t1[t1.length - 1];
            var t3 = nsf.substring(0, nsf.indexOf(t2));
            nsf = String.format("%s%s%s", "^", nsf, "$");
            nsf = nsf.replaceAll("\\*\\.", "*\\\\.").
                    replaceAll("\\*", Constants.WILDCARD_MATCHING_RULE);
            var file = new File(t3);
            if (!file.exists()) {
                throw new NameParseException(String.format("directory %s does not exist", t3));
            }
            for (var f : file.listFiles()) {
                if (!f.isFile()) {
                    continue;
                }
                var path = f.getPath();
                if (path.matches(nsf)) {
                    // 将满足通配符规则的都添加到集合中
                    result.add(path);
                }
            }
            if (result.isEmpty()) {
                throw new NameParseException(String.format("file %s does not exist", sf));
            }
        } else {
            result.add(sf);
        }
        return result;
    }

    /**
     * 通配符相邻去重
     *
     * @param sf
     * @return
     */
    private String duplicateRemoval(String sf) {
        var t1 = sf.indexOf(Constants.WILDCARD);
        if (-1 == t1) {
            return null;
        }
        var left = sf.substring(0, t1);
        var right = sf.substring(t1);
        var t2 = "";
        for (var i = 0; i < right.length(); i++) {
            if (t2.isBlank()) {
                t2 += right.charAt(0);
                continue;
            }
            if (right.charAt(i) != Constants.WILDCARD) {
                t2 += right.charAt(i);
            } else {
                // 如果是通配符'*',则进行去重比较
                if (t2.charAt(t2.length() - 1) != right.charAt(i)) {
                    t2 += right.charAt(i);
                }
            }
        }
        right = t2;
        return String.format("%s%s", left, right);
    }

    private void release(ParamDTO param) {
        Arrays.fill(param.getSecretKey(), ' ');
    }
}
