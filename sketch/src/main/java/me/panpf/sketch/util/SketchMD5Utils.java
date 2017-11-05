/*
 * Copyright (C) 2017 Peng fei Pan <sky@panpf.me>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.panpf.sketch.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SketchMD5Utils {
    /**
     * 默认的密码字符串组合，用来将字节转换成 16 进制表示的字符,apache校验下载的文件的正确性用的就是默认的这个组合
     */
    private static final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static ObjectPool<MessageDigest> digestObjectPool = new ObjectPool<MessageDigest>(new ObjectPool.ObjectFactory<MessageDigest>() {
        @Override
        public MessageDigest newObject() {
            try {
                return MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return null;
            }
        }
    }, 3);

    public static String md5(File file) throws IOException {
        MessageDigest digest = digestObjectPool.get();

        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);

            byte[] reads = new byte[8192];
            int length;
            while ((length = inputStream.read(reads)) != -1) {
                digest.update(reads, 0, length);
            }

            byte[] bytes = digest.digest();
            return bufferToHex(bytes, 0, bytes.length);
        } finally {
            SketchUtils.close(inputStream);

            digest.reset();
            digestObjectPool.put(digest);
        }
    }

    public static String md5(String txt) {
        MessageDigest digest = digestObjectPool.get();

        byte[] textBytes = txt.getBytes();
        digest.update(textBytes);

        byte[] md = digest.digest();
        int j = md.length;
        char str[] = new char[j * 2];
        int k = 0;
        for (byte byte0 : md) {
            str[k++] = hexDigits[byte0 >>> 4 & 0xf];
            str[k++] = hexDigits[byte0 & 0xf];
        }
        String result = new String(str);

        digest.reset();
        digestObjectPool.put(digest);
        return result;
    }

    private static String bufferToHex(byte bytes[], int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }

    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        // 取字节中高 4 位的数字转换, >>>
        char c0 = hexDigits[(bt & 0xf0) >> 4];
        // 为逻辑右移，将符号位一起右移,此处未发现两种符号有何不同
        // 取字节中低 4 位的数字转换
        char c1 = hexDigits[bt & 0xf];
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }
}
