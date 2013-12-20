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

import org.free_erp.jeasyrpc.CallArgUtilities;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author afa
 */
public class ByteReader
{
    private int index = 0;
    private int length;
    private byte[] bytes;

    //预先提供长度是为了性能考虑,先不考虑自动增长
    public ByteReader(byte[] bytes)
    {
        this.bytes = bytes;
    }

    public String readString()
    {
        byte[] lengthBytes = readBytes(4);
        int len = CallArgUtilities.getInt(lengthBytes);
        if (len == 0)
        {
            return null;
        }
        return CallArgUtilities.getString(readBytes(len));
    }

    public List<byte[]> readArray()
    {
        int count = this.readInt();
        if (count == 0)
        {
            return null;
        }
        List<byte[]> list = new ArrayList<byte[]>(count);
        for(int i = 0; i < count; i++)
        {
            int len = this.readInt();
            byte[] bs = this.readBytes(len);
            list.add(bs);
        }
        return list;
    }

    public byte[] readBytes(int length)
    {
        byte[] bs = new byte[length];
        for(int i = 0; i < length; i++)
        {
            bs[i] = bytes[index + i];
        }
        index += length;
        return bs;
    }

    public int readInt()
    {
         byte[] lengthBytes = readBytes(4);
         return CallArgUtilities.getInt(lengthBytes);
    }

    public void close()
    {
        this.bytes = null;
    }

}
