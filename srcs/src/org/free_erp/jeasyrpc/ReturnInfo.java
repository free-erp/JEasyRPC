/*
 * Copyright 2013, TengJianfa , and other individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.free_erp.jeasyrpc;

/**
 *
 * @author afa
 */
public class ReturnInfo extends DataInfo {

    private byte[] returnBytes;

    public ReturnInfo() {
        this.callType = NET_CALL_RETURN;
    }

    public ReturnInfo(byte[] bytes) {
        int index = 0;
        if (bytes[index] != NET_ID) {
            throw new RuntimeException("格式错误,不能识别");
        }
        index++;
        callType = bytes[index];
        index++;
        if (callType == NET_CALL_RETURN) {
            int methodNameLength = bytes[index];
            index++;
            byte[] methodNameBytes = new byte[methodNameLength];
            for (int i = 0; i < methodNameLength; i++) {
                methodNameBytes[i] = bytes[index];
                index++;
            }
            methodName = new String(methodNameBytes);
            //返回长度
            int returnLen = ((bytes[0] & 0xff) << 8) | (bytes[1] & 0xff);
            index++;
            index++;
            //返回值
            if (returnLen == 0) {
                return;
            }
            this.returnBytes = new byte[returnLen];
            for (int j = 0; j < returnLen; j++) {
                returnBytes[j] = bytes[index];
                index++;
            }
        } else {
            throw new RuntimeException("格式错误,不能识别");
        }

    }

    public byte[] toBytes() {
        byte[] methodNameBytes = methodName.getBytes();
        int rLen = returnBytes == null ? 0 : returnBytes.length;
        int byteLen = 3 + methodNameBytes.length + 2 + rLen;//先实现一个最主要的
        byte[] bytes = new byte[byteLen];
        int index = 0;
        //识别码
        bytes[index] = NET_ID;
        index++;
        //调用类型
        bytes[index] = callType;
        index++;
        //methodName
        //方法名长度
        byte len = (byte) methodNameBytes.length;
        bytes[index] = len;
        index++;
        //方法名
        for (int i = 0; i < methodNameBytes.length; i++) {
            bytes[index] = methodNameBytes[i];
            index++;
        }
        //返回参数长度
        if (returnBytes == null) {
            //无返回值
            return bytes;
        }
        int lens = returnBytes.length;
        bytes[index] = (byte) (lens >> 8 & 0xff);//高位放前
        index++;
        bytes[index] = (byte) (lens & 0xff);
        index++;
        //参数存储
        for (int j = 0; j < lens; j++) {
            bytes[index] = returnBytes[j];
            index++;
        }
        return bytes;
    }

    public byte[] getReturnBytes() {
        return returnBytes;
    }

    public void setReturnBytes(byte[] returnBytes) {
        this.returnBytes = returnBytes;
    }

    public void setReturnArg(CallArg arg) {
        if (arg == null) {
            this.returnBytes = null;
        } else {
            this.returnBytes = arg.toBytes();
        }
    }

    public void setReturnArg(byte[] arg) {
        if (arg == null) {
            this.returnBytes = null;
        } else {
            this.returnBytes = arg;
        }
    }
}
