package com.allin.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * NIOClient <br>
 *
 * @author 王俊伟 wjw.happy.love@163.com
 * @date 2016年7月21日 下午5:26:25
 */
public class NIOClient {
    private static final int SIZE = 1024;
    private static final NIOClient instance = new NIOClient();
    public String IP = "127.0.0.1";// 10.50.200.120
    public int CLIENT_PORT = 4444;// 4444 9666
    String encoding = System.getProperty("file.encoding");
    Charset charset = Charset.forName(this.encoding);
    private SocketChannel channel;
    private Selector selector = null;

    private NIOClient() {
    }

    public static NIOClient getInstance() {
        return instance;
    }

    public static void main(final String[] args) {
        try {
            final NIOClient nio = new NIOClient();
            nio.send("test\n");//向服务端发送数据
            //nio.send("metrics:memory:	swap:	cpu:	network i/o:	disks i/o:	tcp:\n");
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public void send(final String content) throws IOException {
        this.selector = Selector.open();
        this.channel = SocketChannel.open();
        // channel = SocketChannel.open(new InetSocketAddress(IP,CLIENT_PORT));
        final InetSocketAddress remote = new InetSocketAddress(this.IP, this.CLIENT_PORT);
        this.channel.connect(remote);
        // 设置该sc以非阻塞的方式工作
        this.channel.configureBlocking(false);
        // 将SocketChannel对象注册到指定的Selector
        // SelectionKey.OP_READ | SelectionKey.OP_WRITE | SelectionKey.OP_CONNECT
        this.channel.register(this.selector, SelectionKey.OP_READ);//这里注册的是read读，即从服务端读数据过来
        // 启动读取服务器数据端的线程
        new ClientThread().start();
        this.channel.write(this.charset.encode(content));
        // 创建键盘输入流
        final Scanner scan = new Scanner(System.in);//这里向服务端发送数据，同时启动了一个键盘监听器
        while (scan.hasNextLine()) {
            System.out.println("输入数据:\n");
            // 读取键盘的输入
            final String line = scan.nextLine();
            // 将键盘的内容输出到SocketChanenel中
            this.channel.write(this.charset.encode(line));
        }
        scan.close();
    }

    /**
     * 客户端发送数据
     *
     * @param channel
     * @param bytes
     * @throws Exception
     */
    protected void sendData(final SocketChannel channel, final byte[] bytes) throws Exception {
        final ByteBuffer buffer = ByteBuffer.wrap(bytes);
        channel.write(buffer);
        //channel.socket().shutdownOutput();
    }

    protected void sendData(final SocketChannel channel, final String data) throws Exception {
        this.sendData(channel, data.getBytes());
    }

    /**
     * 接受服务端的数据
     *
     * @param channel
     * @return
     * @throws Exception
     */
    protected void receiveData(final SocketChannel channel) throws Exception {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
        int count = 0;
        while ((count = channel.read(buffer)) != -1) {
            if (count == 0) {
                Thread.sleep(100); // 等等一下
                continue;
            }
            // 转到最开始
            buffer.flip();
            while (buffer.remaining() > 0) {
                System.out.print((char) buffer.get());
            }
            buffer.clear();
        }
    }

    /**
     * 从服务端读入数据的线程 <br>
     *
     * @author 王俊伟 wjw.happy.love@163.com
     * @date 2016年10月20日 下午9:59:11
     */
    private class ClientThread extends Thread {
        @Override
        public void run() {
            try {
                while (NIOClient.this.selector.select() > 0) {
                    // 遍历每个有可能的IO操作的Channel对银行的SelectionKey
                    for (final SelectionKey sk : NIOClient.this.selector.selectedKeys()) {
                        // 删除正在处理的SelectionKey
                        NIOClient.this.selector.selectedKeys().remove(sk);
                        // 如果该SelectionKey对应的Channel中有可读的数据
                        if (sk.isReadable()) {
                            // 使用NIO读取Channel中的数据
                            final SocketChannel sc = (SocketChannel) sk.channel();
                            String content = "";
                            final ByteBuffer bff = ByteBuffer.allocate(SIZE);
                            while (sc.read(bff) > 0) {
                                sc.read(bff);
                                bff.flip();
                                content += NIOClient.this.charset.decode(bff);
                            }
                            // 打印读取的内容
                            System.out.println("服务端返回数据:" + content);
                            // 处理下一次读
                            sk.interestOps(SelectionKey.OP_READ);
                        }
                    }
                }

            } catch (final IOException io) {
                io.printStackTrace();
            }
        }
    }

    /**
     * TCP 处理 线程<br>
     */
    class TCPClientReadThread implements Runnable {
        private final Selector selector;

        public TCPClientReadThread(final Selector selector) {
            this.selector = selector;
            new Thread(this).start();
        }

        @Override
        public void run() {
            try {
                NIOClient.this.channel.configureBlocking(false);
                // selector.select(3000);
                NIOClient.this.channel.register(this.selector, SelectionKey.OP_READ);

                while (true) {
                    if (this.selector.select(1000) > 0) {
                        // 遍历每个有可用IO操作Channel对应的SelectionKey
                        for (final SelectionKey sk : this.selector.selectedKeys()) {
                            // 如果该SelectionKey对应的Channel中有可读的数据
                            if (sk.isReadable()) {
                                // 使用NIO读取Channel中的数据
                                final SocketChannel sc = (SocketChannel) sk.channel();
                                // 将字节转化为为UTF-8的字符串
                                receiveData(sc);
                                // 为下一次读取作准备
                                sk.interestOps(SelectionKey.OP_READ);
                            } else if (sk.isWritable()) {
                                // 取消对OP_WRITE事件的注册
                                final ByteBuffer buffer = ByteBuffer.allocate(1024);
                                sk.interestOps(sk.interestOps() & (~SelectionKey.OP_WRITE));
                                final SocketChannel sc = (SocketChannel) sk.channel();

                                // 此步为阻塞操作，直到写入操作系统发送缓冲区或者网络IO出现异常
                                // 返回的为成功写入的字节数，若缓冲区已满，返回0
                                final int writeenedSize = sc.write(buffer);

                                // 若未写入，继续注册感兴趣的OP_WRITE事件
                                if (writeenedSize == 0) {
                                    sk.interestOps(sk.interestOps() | SelectionKey.OP_WRITE);
                                }
                            } else if (sk.isConnectable()) {
                                final SocketChannel sc = (SocketChannel) sk.channel();
                                sc.configureBlocking(false);

                                // 注册感兴趣的IO事件，通常不直接注册写事件，在发送缓冲区未满的情况下
                                // 一直是可写的，所以如果注册了写事件，而又不写数据，则很容易造成CPU消耗100%
                                // SelectionKey sKey = sc.register(selector,
                                // SelectionKey.OP_READ);

                                // 完成连接的建立
                                sc.finishConnect();
                            }
                            // 删除正在处理的SelectionKey
                            this.selector.selectedKeys().remove(sk);
                        }
                    }
                    if (this.selector.select(1000) <= 0) {
                        Thread.sleep(1000);
                        continue;
                    }
                }
            } catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
