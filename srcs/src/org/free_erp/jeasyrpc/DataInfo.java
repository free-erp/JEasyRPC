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

import java.nio.ByteBuffer;

/**dd
 *在方法名中体现一个序列号,以区分同一方法的多次调用最大长度127位,方法名最大32位,后6位数字表示序列号自行累积
 如saveFoodOrder#102032
 * @author Administrator
 */
public abstract class DataInfo
{
    public static final byte NET_ID = 111;//识别码，只识别该信息
    
    //方法调用，所有的字节流长度低于10kb
    public static final byte NET_CALL = 1;

    //大内容传输用NET_TRANSFER = 3; 管理者切分成多条CallInfo，每条CallInfo的格式
    //1:第一个识别码；2:方法类型; 3:(方法名#序列号#数据批次）长度；4XXXXXX（方法名#序列号#数据批次); 5，6数据长度；XXXXXX数据
    public static final byte NET_TRANSFER = 3;
    
    //方法返回
    public static final byte NET_CALL_RETURN = 2;
    //第一种
    //1:第一个识别码；2:方法类型; 3:方法名长度；4XXXXXX方法名; 5:参数个数; 6,7:第一个参数长度；XXXXXX第一个参数；2个字节：第2个参数长度，。。。数据...
    //第二种，方法返回
    //1:第一个识别码；2:方法类型; 3:方法名长度；4XXXXXX方法名;5， 6返回参数长度；XXXXXX参数
    
    //第三种，数据传输,用别的方式实现
    //1:第一个识别码；2:方法类型; 3:方法名长度；4XXXXXX方法名;5， 6返回参数长度；XXXXXX参数

    protected byte callType = NET_CALL;
    protected String methodName = "";
    // = new ArrayList<byte[]>();

   
    private ByteBuffer writeBuffer;

    public DataInfo()
    {
        
    }


    //不能超过10kb
    public DataInfo(byte[] bytes) //还原
    {
        
    }

    public ByteBuffer toByteBuffer()
    {
        if (this.writeBuffer == null)
        {
             writeBuffer = ByteBuffer.allocate(10240);//10kb
        }
        this.writeBuffer.clear();
        this.writeBuffer.put(this.toBytes());
        this.writeBuffer.flip();
        return writeBuffer;
    }

    public abstract byte[] toBytes();

    public byte getCallType() {
        return callType;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

   

    
    
    
    
   
    
   
    
    
    
}
