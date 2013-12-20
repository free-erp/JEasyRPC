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

package org.free_erp.jeasyrpc.server;


import org.free_erp.jeasyrpc.DataInfo;
import org.free_erp.jeasyrpc.ReturnInfo;
import org.free_erp.jeasyrpc.TransferInfo;
import biz.inspeed.transfer.Food;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 *
 * @author Administrator
 */
public class LogicObject 
{
    private ReturnInfo returnInfo;
    private LogicObjectPool pool;
   
    
    public LogicObject()
    {
        returnInfo = new ReturnInfo();
    }
    public void executeMethod(SocketChannel channel, DataInfo callInfo) throws IOException
    {
        String methodName = callInfo.getMethodName();
        if (methodName.equals("getFood"))
        {
            getFood(channel, callInfo);
        }
        else if (methodName.equals("addFood"))
        {
            addFood(channel, callInfo);
        }
        else if (methodName.equals("testFileTransfer"))
        {
            testFileTransfer(channel, callInfo);
        }
        //System.out.println("come here!");
    }

    public void getFood(SocketChannel channel, DataInfo callInfo) throws IOException
    {
        returnInfo.setMethodName(callInfo.getMethodName());
        //callInfo.
        Food food = new Food();
        food.setFoodId(100034);
        food.setFoodName("相死磁词");
        System.out.println("come hereererererer");
        returnInfo.setReturnBytes(food.toBytes());
        channel.write(returnInfo.toByteBuffer());
    }

    //测试传输文件
    public void testFileTransfer(SocketChannel channel, DataInfo callInfo) throws IOException
    {
        returnInfo.setMethodName(callInfo.getMethodName());
        File file = new File("f:/test.pdf");
        FileInputStream stream = new FileInputStream(file);
//        FileChannel fc = stream.getChannel();
//        ByteBuffer buffer = ByteBuffer.allocate(1024);
//        fc.

        int length = stream.available();
        byte[] bytes = new byte[60000];//240个控制字节，应该没有问题
        int index = 1;
        int count = length / 60000 + 1;
        //10kb//还有些控制字符，所以比10k大
        ByteBuffer writeBuffer2 = ByteBuffer.allocate(60200);
        channel.write(writeBuffer2);//???
         writeBuffer2.flip();
        while(stream.read(bytes) > 0)
        {
            ByteBuffer writeBuffer = ByteBuffer.allocate(60200);
            System.out.println("id:" + index);
            TransferInfo info = new TransferInfo(callInfo.getMethodName(), index, count, bytes);
            writeBuffer.put(info.toBytes());
            writeBuffer.flip();
            channel.write(writeBuffer);
            try
            {
                Thread.currentThread().sleep(10);
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
            writeBuffer.clear();
            index++;
        }
        stream.close();
    }


    public void addFood(SocketChannel channel, DataInfo callInfo) throws IOException
    {
//        returnInfo.setMethodName(callInfo.getMethodName());
//        count++;
//        String v = "我很好" + count + " 业务对象id:" + this.toString();
//        returnInfo.setReturnBytes(v.getBytes());
//        channel.write(returnInfo.toByteBuffer());
    }
    
    public void setContainer(LogicObjectPool pool)
    {
        this.pool = pool;
    }
    
    public void close()
    {
        if (pool != null)
        {
            pool.returnObject(this);
        }
    }
}
