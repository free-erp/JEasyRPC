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
package org.free_erp.jeasyrpc.client;

import org.free_erp.jeasyrpc.CallInfo;
import org.free_erp.jeasyrpc.DataInfo;
import org.free_erp.jeasyrpc.ReturnInfo;
import org.free_erp.jeasyrpc.TransferInfo;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 *
 * @author afa
 */
public class SocketConnection {

    protected Socket socket;
    protected String ip;
    protected int port;
    protected SocketCallbackListener listener;

    protected MethodCallThread methodCallThread;
    protected CallReturnThread callReturnThread;
    protected TransferThread transferThread;


    public SocketConnection(String ip, int port, SocketCallbackListener listener) {
        this.ip = ip;
        this.port = port;
        this.listener = listener;
        methodCallThread = new MethodCallThread();
        callReturnThread = new CallReturnThread();
        transferThread = new TransferThread();

    }

    public void sendCall(DataInfo info) throws IOException {
        OutputStream op = socket.getOutputStream();
        op.write(info.toBytes());
    }

    public Socket getSocket() {
        return this.socket;
    }

    public void start() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    startSocket();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        thread.start();
    }

    class CallReturnThread extends Thread {

        private SocketCallbackListener listener;
        private ReturnInfo callReturn;

        public CallReturnThread() {
        }

        public synchronized void runThread(SocketCallbackListener listener, ReturnInfo info) {
            this.listener = listener;
            this.callReturn = info;
            this.start();
        }

        public void run()
        {
            listener.methodReturned(callReturn);
        }
    }

    class TransferThread extends Thread {

        private SocketCallbackListener listener;
        private TransferInfo dataInfo;
        public TransferThread() {
            
        }

        public synchronized void runThread(SocketCallbackListener listener, TransferInfo info)
        {
            this.listener = listener;
            this.dataInfo = info;
            this.start();
        }

        public void run() {
                listener.dataTransfered(dataInfo);
            }
        }    

    class MethodCallThread extends Thread {

        private SocketCallbackListener listener;
        private CallInfo dataInfo;

        public MethodCallThread()
        {
        }

        public synchronized void runThread(SocketCallbackListener listener, CallInfo info) {
            this.listener = listener;
            this.dataInfo = info;
            this.start();
        }

        public void run() {
                listener.methodCall(dataInfo);
        }
    }

    //三种线程来分别处理，方法返回，文件传输和方法调用（考虑到线程同步问题,不用线程池,并考虑资源同步)
    private void startSocket() throws IOException {
        socket = new Socket(ip, port);
        InputStream stream = socket.getInputStream();
        byte[] values = new byte[64000];
        int len = stream.read(values);
        while (len > 0) {
            //this.outStrings.add("客户端:" + socket.getLocalPort() + "  received count:" + len + "\n");
            len = stream.read(values);
            if (values[0] == DataInfo.NET_ID && values[1] == DataInfo.NET_CALL_RETURN) {
                ReturnInfo info = new ReturnInfo(values);
                this.callReturnThread.runThread(listener, info);
            } else if (values[0] == DataInfo.NET_ID && values[1] == DataInfo.NET_TRANSFER) {
                TransferInfo info = new TransferInfo(values);
                transferThread.runThread(listener, info);
//            Thread thread = new Thread() {
//            @Override
//            public void run() {
//                try {
 //               listener.dataTransfered(info);
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//            }
//        };
//            thread.start();
//            }
            } else if (values[0] == DataInfo.NET_ID && values[1] == DataInfo.NET_TRANSFER) {
                CallInfo info = new CallInfo(values);
                methodCallThread.runThread(listener, info);
            }
        }
        socket.close();
    }
}
