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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author afa
 */
public class CallInfo extends DataInfo {

    private List<byte[]> args;

    public CallInfo()//新构建
    {
        this.callType = NET_CALL;
        args = new ArrayList<byte[]>();
    }

    public CallInfo(byte[] bytes) {
        int index = 0;
        if (bytes[index] != NET_ID) {
            throw new RuntimeException("格式错误,不能识别");
        }
        index++;
        callType = bytes[index];
        index++;
        if (callType == NET_CALL) {
            args = new ArrayList<byte[]>();
            int methodNameLength = bytes[index];
            index++;
            byte[] methodNameBytes = new byte[methodNameLength];
            for (int i = 0; i < methodNameLength; i++) {
                methodNameBytes[i] = bytes[index];
                index++;
            }
            methodName = new String(methodNameBytes);
            int argCount = bytes[index];
            index++;
            for (int i = 0; i < argCount; i++) {
                //参数长度
                int argLen = (bytes[index] & 0xff << 8) | ((bytes[index + 1]) & 0xff);
                index++;
                index++;
                //参数
                if (argLen == 0)//参数类型为空
                {
                    continue;
                }
                byte[] arg = new byte[argLen];
                for (int j = 0; j < argLen; j++) {
                    arg[j] = bytes[index];
                    index++;
                }
                args.add(arg);
            }
        }
    }

    public List<byte[]> getArgs() {
        return args;
    }

    public void addArg(byte[] arg) {
        args.add(arg);
    }

    public void addCallArg(CallArg arg) {
        args.add(arg.toBytes());
    }

    public void addIntArg(int arg) {
        args.add(CallArgUtilities.getIntBytes(arg));
    }

    public void addShortArg(short arg) {
        args.add(CallArgUtilities.getShortBytes(arg));
    }

    public void addShortArg(long arg) {
        args.add(CallArgUtilities.getLongBytes(arg));
    }

    public void addStringArg(String arg) {
        args.add(CallArgUtilities.getStringBytes(arg));
    }

    public byte[] toBytes() {

        byte[] methodNameBytes = methodName.getBytes();
        int byteLen = 3 + methodNameBytes.length + 1 + args.size() * 2;//先实现一个最主要的
        for (byte[] arg : args) {
            if (arg == null) {
                continue;
            }
            byteLen += arg.length;
        }
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
        //参数个数
        bytes[index] = (byte) args.size();
        index++;
        //...
        //长度最大接受65536个字节
        //每个参数
        for (int i = 0; i < args.size(); i++) {
            //参数长度
            byte[] arg = args.get(i);
            if (arg == null)//传入null,不存任何数据
            {
                int lens = 0;
                bytes[index] = (byte) (lens >> 8 & 0xff);//高位放前
                index++;
                bytes[index] = (byte) (lens & 0xff);
                index++;
                continue;
            }
            int lens = arg.length;
            bytes[index] = (byte) ((lens & 0xff) >> 8);//高位放前
            index++;
            bytes[index] = (byte) (lens & 0xff);
            index++;
            //参数存储
            for (int j = 0; j < lens; j++) {
                bytes[index] = arg[j];
                index++;
            }
        }
        return bytes;

    }
}
