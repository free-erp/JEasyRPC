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

import org.free_erp.jeasyrpc.CallArg;
import org.free_erp.jeasyrpc.CallArgUtilities;

/**
 *
 * @author afa
 */
public class ByteWriter
{
    private int index = 0;
    private int length;
    private byte[] bytes;

    //预先提供长度是为了性能考虑,先不考虑自动增长
    public ByteWriter(int length)
    {
        this.length = length;
        bytes = new byte[length];
    }

     public ByteWriter()
    {
        this.length = 1000;
        bytes = new byte[length];
    }
     
    public void writeString(String value)
    {
        if (value == null)
        {
            this.writeInt(0);
            return;
        }
        byte[] bs = CallArgUtilities.getStringBytes(value);
        this.writeBytes(CallArgUtilities.getIntBytes(bs.length));
        this.writeBytes(bs);
    }

    public void writeBytes(byte[] bs)
    {
        for(int i = 0; i < bs.length; i++)
        {
            bytes[index + i] = bs[i];
        }
        index += bs.length;
    }

    public void writeInt(int value)
    {
        this.writeBytes(CallArgUtilities.getIntBytes(value));
    }

    /**
     * 为了考虑通用，不设置成CallArg类型,但传入的必须为CallArg对象数组
     * @param args
     */
    public void writeArray(Object[] args)
    {
        if (args == null || args.length == 0)
        {
            this.writeInt(0);
            return;
        }
        this.writeInt(args.length);
        for(int i = 0; i < args.length; i++)
        {
            CallArg arg = (CallArg)args[i];
            byte[] bs = arg.toBytes();
            this.writeInt(bs.length);
            this.writeBytes(bs);
        }
    }
    public byte[] getTrimedBytes()
    {
        byte[] realBytes = new byte[index + 1];
        for(int i = 0; i < index; i++)
        {
            realBytes[i] = bytes[i];
        }
        return realBytes;
    }

    public byte[] getBytes()
    {
        return bytes;
    }

    public void close()
    {
        this.bytes = null;
    }
}
