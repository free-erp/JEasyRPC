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

import org.free_erp.jeasyrpc.CallInfo;
import org.free_erp.jeasyrpc.DataInfo;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Stack;

/**
 *设计思想，主线程不停循环，用来创建连接，读写用多个线程来运行，读动作通过线程池来提高性能
 * @author Administrator
 */
public class NIOServer {

    ServerSocketChannel serverSocketChannel;
    ServerSocket serverSocket;
    Selector selector;
    private final int PORT = 7034;
    private ReadThreadPool readThreadPool;
    private boolean runState = true;

    private static NIOServer nioServer;
    
    public static NIOServer getServer() throws IOException 
    {
        if (nioServer == null)
        {
            nioServer = new NIOServer();
        }
        return nioServer;
    }
    
    private NIOServer() throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocket = serverSocketChannel.socket();
        this.readThreadPool = new ReadThreadPool();
        InetSocketAddress address = new InetSocketAddress(PORT);
        serverSocket.bind(address);
        serverSocketChannel.configureBlocking(false);
        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Server started!");
    }
    
    public void close() throws IOException 
    {
        runState = false;
        serverSocketChannel.close();
    }

    public void listen() throws IOException {
//        ByteBuffer buffer = ByteBuffer.allocate(1024);
//        ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
        while (runState) {
            int count = selector.select();
            //
//            if (count <= 0)
//            {
//                continue;
//            }
            //System.out.println("count:" + count + " selector.keys().count:" + selector.keys().size());
            if (selector.keys().size() <= 0) {
                continue;
            }
            Object[] keys = selector.keys().toArray();//.iterator();
            //System.out.println("size:" + keys.length);
            for (Object okey : keys) {
                SelectionKey key = (SelectionKey) okey;
                int ops = key.readyOps();
                if ((ops & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT)//key.isAcceptable())
                {
                    //ServerSocketChannel serverChannel = (ServerSocketChannel)key.channel();
                    SocketChannel channel = this.serverSocketChannel.accept();
                    if (channel != null) {
                        channel.configureBlocking(false);
                        channel.register(selector, SelectionKey.OP_READ);
//                        ByteBuffer b = ByteBuffer.allocate(1024);
//                        b.put("welcome to beijing!".getBytes());
//                        b.flip();
//                        channel.write(b);
                    }
                    //keys.remove();

                } else if ((ops & SelectionKey.OP_READ) == SelectionKey.OP_READ)//key.isReadable())//)
                {
                    //System.out.println("有新的读取!");
                    SocketChannel channel = (SocketChannel) key.channel();
                    ReadThread readThread = this.readThreadPool.getThread();
                    readThread.initRunArgs(channel, key); //创建线程读，减少主线程，连接数创建的困难
                    readThread.run();
                }
            }
        }
    }
    
    //需要能监控读当前线程个数
    class ReadThreadPool
    {
        Stack<ReadThread> threads;
        //
        private final int INIT_COUNT = 32;
        public ReadThreadPool()
        {
            threads = new Stack<ReadThread>();
            for(int i = 0; i < INIT_COUNT; i++)
            {
                ReadThread thread = new ReadThread(this);
                threads.push(thread);
            }
        }
        public ReadThread getThread()
        {
            if (this.threads.size() <= 0)
            {
                for(int i = 0; i < INIT_COUNT; i++)
                {
                    ReadThread thread = new ReadThread(this);
                    threads.push(thread);
                }
            }
            return threads.pop();
        }
        
        public void returnThread(ReadThread thread) //回收先不考虑,很久保持很高的容量，就需要回收，不能只增不减
        {
            thread.clear();
            this.threads.push(thread);
        }
        
    }
    
    //考虑线程池,可以优化，在主线程中减少创建实例的动作
    class ReadThread extends Thread
    {
        private SocketChannel channel;
        private SelectionKey key;
        private ReadThreadPool pool;
        
        public ReadThread(ReadThreadPool p)
        {
            this.pool = p;
        }
        
        //为读线程池准备的,在调用前必须先初始化
        public void initRunArgs(SocketChannel channel,SelectionKey key)
        {
            this.channel = channel;
            this.key = key;
        }
        
        //归还
        public void clear()
        {
            channel = null;
            key = null;
            
        }
        
        public void close()
        {
            clear();
            pool.returnThread(this);
        }
        
        public void run() {
            if (channel == null || key == null)
            {
                throw new RuntimeException("没有对channel和key初始化，请先调用initRunArgs方法");
            }
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                try {
                    int c = channel.read(buffer);
                    if (c <= 0)
                    {
                        close();
                        return;
                    }

                    CallInfo info = new CallInfo(buffer.array());
                        LogicObject obj = LogicObjectPool.getLogicObjectPool().getLogicObject();
                        try {
                            obj.executeMethod(channel, info);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            obj.close();
                        }
                    
                } catch (java.io.IOException ex) {
                    key.cancel();
                    try {
                        channel.finishConnect();
                    } catch (Exception ex2) {
                        ex2.printStackTrace();
                    }
                }
                finally
                {
                    this.close();
                }
           }
    }

    public void createThreadCall(final SocketChannel channel, final byte[] array) {
        if (array[0] != DataInfo.NET_ID)
        {
            return;
        }
        Thread thread = new Thread() {
            public void run() {
                CallInfo info = new CallInfo(array);
                 LogicObject obj = LogicObjectPool.getLogicObjectPool().getLogicObject();
                    try {
                        obj.executeMethod(channel, info);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        obj.close();
                    }

//                String methodName = info.getMethodName();
//                if (methodName.equals("saveSomething")) {
//                    LogicObject obj = LogicObjectPool.getLogicObjectPool().getLogicObject();
//                    try {
//                        obj.executeMethod(channel, info);
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                        obj.close();
//                    }
                
            }
        };
        thread.run();
    }

    private void printBuffer(ByteBuffer buffer, int start, int len) {
        buffer.flip();
        System.out.print(new String(buffer.array(), 0, len));
//        for(int i = 0; i < buffer.limit(); i++)
//        {
//            System.out.print(buffer.get(i) + ",");
//        }
    }

    public static void main(String args[]) {
        try {
            NIOServer server = NIOServer.getServer();
            server.listen();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
