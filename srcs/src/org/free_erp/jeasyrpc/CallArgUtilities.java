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
 *注意：移位运算由于位运算(&,|)
 * @author Administrator
 */
public class CallArgUtilities 
{
    public static CallArg transInt(int value)
    {
        return new BaseCallArg(value);
    }
    
    public static CallArg transByte(byte value)
    {
        return new BaseCallArg(value);
    }
    
    public static CallArg transShort(short value)
    {
        return new BaseCallArg(value);
    }
    
    public static CallArg transString(String value)
    {
        return new BaseCallArg(value);
    }
    
    public static CallArg transLong(Long value)
    {
        return new BaseCallArg(value);
    }
    
    public static int getInt(byte[] bytes)
    {
        if (bytes == null || bytes.length != 4)
        {
            return 0;//throw new RuntimeException("类型不对!");
        }
        return ((bytes[0] & 0xff) << 24) | ((bytes[1] & 0xff) << 16) | ((bytes[2] & 0xff) << 8) | (bytes[3] & 0xff);
     }
    
    public static byte[] getIntBytes(int value)
    {
        byte[] bytes = new byte[4];
        bytes[0] = (byte)(value>>24 & 0xff);
        bytes[1] = (byte)(value>>16 & 0xff);
        bytes[2] = (byte)(value>>8 & 0xff);    

        bytes[3] = (byte)(value & 0xff) ;
        return bytes;
    }
    
    public static byte getIntLowByte(int value)
    {
        return (byte)(value & 0xff);
    }
    
    public static byte[] getLongBytes(long value)
    {
        byte[] bytes = new byte[8];
        bytes[0] = (byte)(value>>56 & 0xff);
        bytes[1] = (byte)(value>>48 & 0xff);
        bytes[2] = (byte)(value>>40 & 0xff);
        bytes[3] = (byte)(value>>32 & 0xff);
        bytes[4] = (byte)(value>>24 & 0xff);
        bytes[5] = (byte)(value>>16 & 0xff);
        bytes[6] = (byte)(value>>8 & 0xff);
        bytes[7] = (byte)(value & 0xff);
        return bytes;
    }
    
    public static byte[] getShortBytes(short value)
    {
        byte[] bytes = new byte[2];
        bytes[0] = (byte)(value>>8 & 0xff);
        bytes[1] = (byte)(value & 0xff);
        return bytes;
    }
    
    
    
    public static long getLong(byte[] bytes)
    {
         if (bytes == null || bytes.length != 8)
        {
            return 0;//throw new RuntimeException("类型不对!");
        }
        long v1 = bytes[0] & 0xff;
        long v2 = bytes[1] & 0xff;
        long v3 = bytes[2] & 0xff;
        long v4 = bytes[3] & 0xff;
        long v5 = bytes[4] & 0xff;
        long v6 = bytes[5] & 0xff;
        long v7 = bytes[6] & 0xff;
        long v8 = bytes[7] & 0xff;
        return (v1 << 56) | (v2 << 48) | (v3 << 40) | (v4 << 32) | (v5 << 24) | (v6 << 16) |  (v7 << 8) | (v8 & 0xff);
    }
    
    public static byte[] getStringBytes(String value)
    {
         return value.getBytes();
//         byte[] bytes = new byte[value.length() * 2];
//         int index = 0;
//         for(int i = 0; i < value.length(); i++)
//         {
//             char ch = value.charAt(i);
//             bytes[index] = (byte)(ch  & 0xff >> 8);
//             index++;
//             bytes[index] = (byte)(ch & 0xff);
//             index++;
//         }
//         return bytes;
    }
    
    public static String getString(byte[] bytes)
    {
        if (bytes == null)
        {
            return null;
        }
        return new String(bytes);
        /*
        char[] chs = new char[bytes.length / 2];
        int index = 0;
        String v = "";
        for(int i = 0; i < bytes.length; i++)
        {
            chs[index] = (char)((bytes[i] & 0xff << 8) | (bytes[i + 1] & 0xff));
            v += chs[index];
            i++;
            index++;
        }
        System.out.println("vvvv:" + v);
        return new String(chs);
         * 
         */
    }
    
    public static short getShort(byte[] bytes)
    {
        if (bytes.length != 2)
        {
            throw new RuntimeException("类型不对!");
        }
        return (short)(((bytes[0] & 0xff) << 8) | (bytes[1] & 0xff));

    }

    public static TransferInfo[] seperateTransferArray(String methodName, byte[] bytes, int bytesSegment)
    {
        int count = bytes.length / bytesSegment + 1;
        TransferInfo[] infos = new TransferInfo[count];
        int currentIndex = 1;
        int index = 0;
        for(int i = 0; i < count; i++)
        {
            int len = bytesSegment;
            if (i == count - 1)
            {
                len = bytes.length % bytesSegment;
            }
             byte[] bs = new byte[len];
             for(int j = 0; j < len; j++)
             {
                 bs[j] = bytes[index];
                 index++;
             }
             infos[i] = new TransferInfo(methodName, currentIndex, count, bs);
             currentIndex++;
        }
        return infos;
    }
   
    public static void main(String args[])
    {
        short abc = (39 & 0xff)  << 8;
         int bbb = abc | (16 & 0xff);
         System.out.println("abbbb:" + bbb);
        short a = 10000;
        byte[] bs = CallArgUtilities.getShortBytes(a);
        System.out.println("valuedddddddddddddddddddddd:" + CallArgUtilities.getShort(bs));
        /*
        String testString = "i am very happy to meet you again!sdkjfksjdfkjsdkfjskdfjsdf,sdjfksdjfksdjfksdjfksdjfksdjfksdjfksjdkfjsdkfjsdkfjksdfjksdjfksjfksjfksjfksjfksjfksjfksfjk";
        byte[] bss = CallArgUtilities.getStringBytes(testString);
        TransferInfo[] infos = CallArgUtilities.seperateTransferArray("sendFoods", bss, 20);
        ByteWriter writer = new ByteWriter(1024);
        for(int i = 0; i < infos.length; i++)
        {
            System.out.println("size:" + infos[i].getCurrentIndex() + "/" + infos[i].getTotalCount());
            System.out.println("v:" + CallArgUtilities.getString(infos[i].getTransferBytes()));
            byte[] tes = infos[i].toBytes();
            TransferInfo is = new TransferInfo(tes);
            System.out.println("index/count=" + is.getCurrentIndex() + "/" + is.getTotalCount());
            writer.writeBytes(infos[i].getTransferBytes());
        }
        System.out.println("vvv:" + CallArgUtilities.getString(writer.getBytes()));

         *
         */
        //TransferInfo info = new TransferInfo("sendFoods", 12, 23, CallArgUtilities.getStringBytes(testString));
        //byte[] bytes = info.toBytes();
        //TransferInfo i = new TransferInfo(bytes);
        //System.out.println(CallArgUtilities.getString(i.getTransferBytes()));


        //方法名public void saveArgs(int a, int b, String good);
        /*
        CallInfo callInfo = new CallInfo();
        callInfo.setMethodName("saveArgs");
        callInfo.addIntArg(0);
        
        callInfo.addIntArg(99);
        callInfo.addStringArg("中国人");
        byte[] bytes = callInfo.toBytes();
        
        CallInfo i = new CallInfo(bytes);
        System.out.println("i:" + i.getMethodName());
        System.out.println("methodCount" + i.getArgs().size());
        System.out.println("1:" + CallArgUtilities.getInt(i.getArgs().get(0)));
        System.out.println("2:" + CallArgUtilities.getInt(i.getArgs().get(1)));
        System.out.println("3:" + CallArgUtilities.getString(i.getArgs().get(2)));
         *
         */
        /*
        CallInfo callInfo = new CallInfo();
        callInfo.setCallType(CallInfo.NET_CALL_RETURN);
        callInfo.setMethodName("saveArgs");
        //callInfo.setReturnBytes(CallArgUtilities.getStringBytes("i am good!"));
        byte[] bytes = callInfo.toBytes();
        CallInfo i = new CallInfo(bytes);
        System.out.println("3:" + CallArgUtilities.getString(i.getReturnBytes()));
         * 
         */
    }
}
