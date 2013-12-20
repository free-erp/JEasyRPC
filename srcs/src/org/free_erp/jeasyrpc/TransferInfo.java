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
public class TransferInfo extends DataInfo
{
    private byte[] transferBytes;
    private int totalCount;
    private int currentIndex;
    //private static final int MAX_LEN = 10240;

    public TransferInfo(String methodName, int currentIndex, int totalCount, byte[] bytes)
    {
        super.methodName = methodName;
        this.totalCount = totalCount;
        this.currentIndex = currentIndex;
        this.callType = NET_TRANSFER;
        this.transferBytes = bytes;
    }
    
    public TransferInfo(byte[] bytes)
    {
        int index = 0;
        if (bytes[index] != NET_ID) {
            throw new RuntimeException("格式错误,不能识别");
        }
        index++;
        callType = bytes[index];
        index++;
        if (callType == NET_TRANSFER) {
            int methodNameLength = bytes[index];
            index++;
            byte[] methodNameBytes = new byte[methodNameLength];
            for (int i = 0; i < methodNameLength; i++) {
                methodNameBytes[i] = bytes[index];
                index++;
            }
            String fullMethodName = new String(methodNameBytes);
            //System.out.println("fullMethodName:" + fullMethodName);
            //sendFood#2308#3/8
            int sep = fullMethodName.lastIndexOf("#");
            if (sep > 0)
            {
                methodName = fullMethodName.substring(0, sep);
                String tempString = fullMethodName.substring(sep + 1);
                sep = tempString.indexOf("/");
                if (sep > 0)
                {
                    this.currentIndex = Integer.parseInt(tempString.substring(0, sep));
                    this.totalCount = Integer.parseInt(tempString.substring(sep + 1));
                }
            }
            //返回长度
            int dataLen = ((bytes[index] & 0xff) << 8 ) | ((bytes[index + 1]) & 0xff);
            index++;
            index++;
            //返回值
            if (dataLen == 0) {
                return;
            }
            this.transferBytes = new byte[dataLen];
            for (int j = 0; j < dataLen; j++) {
                transferBytes[j] = bytes[index];
                index++;
            }
        } else {
            throw new RuntimeException("格式错误,不能识别");
        }
    }

    

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
    
    public byte[] toBytes()
    {
        //格式"getFoodDatas#1022#1/349" 49长度
        String mName = methodName + "#" + this.currentIndex + "/" + this.totalCount;
        byte[] methodNameBytes = mName.getBytes();
        int rLen = this.transferBytes == null ? 0 : transferBytes.length;
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
        if (this.transferBytes == null) {
            //无返回值
            return bytes;
        }
        int lens = transferBytes.length;
        bytes[index] = (byte) (lens >> 8 & 0xff);//高位放前
        index++;
        bytes[index] = (byte) (lens & 0xff);
        index++;
        //参数存储
        for (int j = 0; j < lens; j++) {
            bytes[index] = transferBytes[j];
            index++;
        }
        return bytes;
    }

    public byte[] getTransferBytes() {
        return transferBytes;
    }

    public void setTransferBytes(byte[] transferBytes) {
        this.transferBytes = transferBytes;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }
    

}
