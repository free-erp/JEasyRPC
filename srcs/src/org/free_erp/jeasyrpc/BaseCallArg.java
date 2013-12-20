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
 * @author Administrator
 */
public class BaseCallArg implements CallArg
{
    private byte[] bytes;
    private int type;
    public static final int TYPE_LONG = 4;
    public static final int TYPE_INT = 3;
    public static final int TYPE_SHORT = 2;
    public static final int TYPE_BYTE = 1;
    public static final int TYPE_STRING = 4;
    public static final int TYPE_DOUBLE = 5;
    
    
    public BaseCallArg(int value)
    {
        type= TYPE_INT;
        
    }
    
    public BaseCallArg(byte value)
    {
        type= TYPE_BYTE;
        bytes = new byte[1];
        bytes[0] = (byte)value;
    }
    public BaseCallArg(short value)
    {
         type= TYPE_SHORT;
         bytes = CallArgUtilities.getShortBytes(value);
         
    }
    public BaseCallArg(long value)
    {
         type= TYPE_LONG;
         bytes = CallArgUtilities.getLongBytes(value);
         
    }
    public BaseCallArg(String value)
    {
         type= TYPE_STRING;
         bytes = CallArgUtilities.getStringBytes(value);
    }
   
    

    @Override
    public byte[] toBytes() {
        return bytes;
    }

     public void deBytes(byte[] bs) {
       
    }
}
