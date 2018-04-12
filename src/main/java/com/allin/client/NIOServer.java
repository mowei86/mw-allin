package com.allin.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NIOServer {

    public static final int PORT = 4444;
    private static final int SIZE = 256;
    private static final Integer[] cardsBook = {
            24, 23, 22, 21
            , 34, 33, 32, 31
            , 44, 43, 42, 41
            , 54, 53, 52, 51
            , 64, 63, 62, 61
            , 74, 73, 72, 71
            , 84, 83, 82, 81
            , 94, 93, 92, 91
            , 104, 103, 102, 101
            , 114, 113, 112, 111
            , 124, 123, 122, 121
            , 134, 133, 132, 131
            , 144, 143, 142, 141
    };
    public String IP = "127.0.0.1";// 10.50.200.120
    // 对于以字符方式读取和处理的数据必须要进行字符集编码和解码
    String encoding = System.getProperty("file.encoding");
    // 加载字节编码集
    Charset charse = Charset.forName(this.encoding);

    public NIOServer() throws IOException {
        // NIO的通道channel中内容读取到字节缓冲区ByteBuffer时是字节方式存储的，
        // 分配两个字节大小的字节缓冲区
        final ByteBuffer buffer = ByteBuffer.allocate(SIZE);
        SocketChannel ch = null;
        Selector selector = null;
        ServerSocketChannel serverChannel = null;

        try {
            // 打开通道选择器
            selector = Selector.open();
            // 打开服务端的套接字通道
            serverChannel = ServerSocketChannel.open();
            // 将服务端套接字通道连接方式调整为非阻塞模式
            serverChannel.configureBlocking(false);
            // serverChannel.socket().setReuseAddress(true);
            // 将服务端套接字通道绑定到本机服务端端口
            serverChannel.socket().bind(new InetSocketAddress(this.IP, PORT));
            // 将服务端套接字通道OP_ACCEP事件注册到通道选择器上
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Server on port:" + PORT);
            while (true) {
                // 通道选择器开始轮询通道事件
                selector.select();
                final Iterator it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    // 获取通道选择器事件键
                    final SelectionKey skey = (SelectionKey) it.next();
                    it.remove();
                    // 服务端套接字通道发送客户端连接事件，客户端套接字通道尚未连接
                    if (skey.isAcceptable()) {
                        // 获取服务端套接字通道上连接的客户端套接字通道
                        ch = serverChannel.accept();
                        System.out.println("Accepted connection from:" + ch.socket());
                        // 将客户端套接字通过连接模式调整为非阻塞模式
                        ch.configureBlocking(false);
                        // 将客户端套接字通道OP_READ事件注册到通道选择器上
                        ch.register(selector, SelectionKey.OP_READ);
                    }
                    // 如果sk对应的Channel有数据需要读取
                    if (skey.isReadable()) {
                        // 获取该SelectionKey对银行的Channel，该Channel中有刻度的数据
                        final SocketChannel sc = (SocketChannel) skey.channel();
                        String content = "";
                        // 开始读取数据
                        try {
                            content = receiverFromClient(sc, buffer);
                            // 将sk对应的Channel设置成准备下一次读取
                            skey.interestOps(SelectionKey.OP_READ);
                        } catch (final IOException e) {// 如果捕获到该sk对银行的Channel出现了异常，表明
                            // Channel对应的Client出现了问题，所以从Selector中取消
                            // 从Selector中删除指定的SelectionKey
                            skey.cancel();
                            if (skey.channel() != null) {
                                skey.channel().close();
                            }
                        }
                        // 如果content的长度大于0,则处理信息返回给客户端
                        if (content.length() > 0) {
                            System.out.println("接受客户端数据：" + content);
                            // 处理信息返回给客户端
                            sendToClient(selector, content);
                        }
                        //ch.write((ByteBuffer)buffer.rewind());
                        //buffer.clear();
                    }
                    if (skey.isWritable()) {

                    }
                }
            }
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            if (ch != null) {
                ch.close();
            }
            serverChannel.close();
            selector.close();
        }
    }

    public static void main(final String[] args) {

//        final List<Integer> list = getCards(12);
//        list.forEach(System.out::println);
        List<CardPlayer> list = new ArrayList<>();
        CardPlayer c = new CardPlayer();
        c.setUserId(111);
        list.add(c);
        c = new CardPlayer();
        c.setUserId(222);
        list.add(c);
        c = new CardPlayer();
        c.setUserId(333);
        list.add(c);
        c = new CardPlayer();
        c.setUserId(444);
        list.add(c);
        c = new CardPlayer();
        c.setUserId(555);
        list.add(c);
        c = new CardPlayer();
        c.setUserId(666);
        list.add(c);
        c = new CardPlayer();
        c.setUserId(777);
        list.add(c);
        c = new CardPlayer();
        c.setUserId(888);
        list.add(c);
        c = new CardPlayer();
        c.setUserId(999);
        list.add(c);
        list = deal(list, 0);
        list.forEach(CardPlayer -> System.out.println(CardPlayer.getUserId() + ":"
                + CardPlayer.getFirstCard() + "-"
                + CardPlayer.getSecondCard() + "-"
                + CardPlayer.getThirdCard() + ". "
                + CardPlayer.getCardStyle() + "--"
                + CardPlayer.getFightingCapacity()
        ));
    }



    /**
     * 发牌
     *
     * @param
     * @param
     * @throws IOException
     */
    public static List<CardPlayer> deal(List<CardPlayer> peopleList, final int CardsType) {

        //初始化发牌数量
        int cardNo = 3;
        if (CardsType == 0) {
            cardNo = 3;
        }
        //获取总牌数
        final int number = peopleList.size() * cardNo;
        //获取牌坐标
        final List<Integer> indexList = CardTools.getCards(number);

        int i = 0;
        for (int j = 0; j < peopleList.size(); j++) {
            peopleList.get(j).setFirstCard(cardsBook[indexList.get(i)]);
            ++i;
            peopleList.get(j).setSecondCard(cardsBook[indexList.get(i)]);
            ++i;
            peopleList.get(j).setThirdCard(cardsBook[indexList.get(i)]);
            ++i;
        }

        //比较牌的大小
        peopleList = CardTools.compareBigNumber(peopleList);
        return peopleList;


    }


    /**
     * 向客户端发送数据
     *
     * @param selector
     * @param content
     * @throws IOException
     */
    public void sendToClient(final Selector selector, final String content) throws IOException {
        // 遍历selector里注册的所有SelectionKey
        for (final SelectionKey key1 : selector.keys()) {
            // 获取该key对应的Channel
            final Channel targerChannel = key1.channel();
            // 如果该Channel是SocketChannel对象
            if (targerChannel instanceof SocketChannel) {
                // 将读取到的内容写入该Channel中
                final SocketChannel dest = (SocketChannel) targerChannel;
                sendToClient(dest, content);
            }
        }
    }


    //    public static void main(final String[] args) {
//        try {
//            new NIOServer();
//        } catch (final IOException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 向指定频道发送数据
     *
     * @param channel
     * @param data
     * @throws IOException
     */
    public void sendToClient(final SocketChannel channel, final String data) throws IOException {
        channel.write(this.charse.encode(data));
        //channel.socket().shutdownOutput();
    }

    //随机获取一涨牌
//    public int getACard() {
//        //产生0-(arr.length-1)的整数值,也是数组的索引
//        final int index = (int) (Math.random() * cardsBook.length);
//        final int rand = cardsBook[index];
//        return rand;
//    }

    /**
     * 接受来自客户端数据
     *
     * @param channel
     * @param buffer
     * @return
     * @throws Exception
     */
    private String receiverFromClient(final SocketChannel channel, final ByteBuffer buffer) throws Exception {
        String content = "";
        //* 取客户端发送的数据两个方法任选其一即可
        // 开始读取数据
        // 法一
        channel.read(buffer);
        final CharBuffer cb = this.charse.decode((ByteBuffer) buffer.flip());
        content = cb.toString();
        // 法二
		/*
		while (sc.read(buffer) > 0) {
			buffer.flip();
			content += charse.decode(buffer);
		}//*/
        buffer.clear();
        return content;
    }


}
