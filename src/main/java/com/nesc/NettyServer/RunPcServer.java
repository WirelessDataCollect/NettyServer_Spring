package com.nesc.NettyServer;

import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

/**
* 
* 运行TCP服务器，用于连接上位机
*
* @author  nesc528
* @Date    2018-8-29
* @version 0.0.1
*/
public class RunPcServer implements Runnable{
	private Thread t;
	private String threadName = "PC-Thread";
	private int listenPort = 8081;
	public Channel ch = null;
	private volatile static Map<String,Channel> ch_map = new ConcurrentHashMap<String,Channel>();//存储PC连接的通道<PC[num],channel>

	/**
	 * 设置线程名称。bean的set方法，bean会自动调用
	 * 
	 * @param port
	 */
	public void setListenPort(int port) {
		listenPort = port;
		System.out.println("Listen port for PC: "+listenPort);
	}
	/**
	 * 设置线程名称。bean的set方法，bean会自动调用。
	 * 
	 * @param port
	 */	
	public void setThreadName(String name) {
		threadName = name;
	}
	/**
	 * 获取保存同服务器连接的PC的通道
	 * @return {@link Map}
	 */
	public static Map<String,Channel> getChMap(){
		return ch_map;
	}
	/**
	 * 返回连接服务器的PC个数
	 * @return int
	 */
	public int getPcNum(){
		return ch_map.size();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
        EventLoopGroup bossGroup = new NioEventLoopGroup();        // 用来接收进来的连接，这个函数可以设置多少个线程
        EventLoopGroup workerGroup = new NioEventLoopGroup();    // 用来处理已经被接收的连接
        
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)            // 这里告诉Channel如何接收新的连接
            .childHandler( new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {//起初ch的pipeline会分配一个RunPcServer的出/入站处理器（初始化完成后删除）
                    // 自定义处理类 
                    ch.pipeline().addLast(new TCP_ServerHandler4PC());//如果需要继续添加与之链接的handler，则再次调用addLast即可
                    //ch.pipeline().addLast(new TCP_ServerHandler4PC());//这样会有两个TCP_ServerHandler4PC处理器
                }//完成初始化后，删除RunPcServer出/入站处理器
            })
            .option(ChannelOption.SO_BACKLOG, 128)
            .childOption(ChannelOption.SO_KEEPALIVE, true);
           
             
            // 绑定端口，开始接收进来的连接
            ChannelFuture cf = b.bind(listenPort).sync();//在bind后，创建一个ServerChannel，并且该ServerChannel管理了多个子Channel 
            // 等待服务器socket关闭
            ch = cf.channel();
            ch.closeFuture().sync();      
            
        } catch (Exception e) {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
        finally {
        	 workerGroup.shutdownGracefully();
             bossGroup.shutdownGracefully();      	
        }
    }
	/**
	 * 开始RunPcServer对象的线程
	 * @return none
	 */
	public void start () {
		System.out.println("Starting " +  threadName );
		if (t == null) {
			t = new Thread (this, threadName);
			t.start ();
		}
	}
}
/**
* 
* TCP服务器的输入处理器函数(ChannelHandler)
*
* @author  nesc528
* @Date    2018-8-29
* @version 0.0.1
*/
class TCP_ServerHandler4PC  extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            ByteBuf in = (ByteBuf)msg;
            System.out.println("Recv from PC:"+in.toString(CharsetUtil.UTF_8));
            ctx.writeAndFlush(Unpooled.copiedBuffer(in));
            
        } finally {
            // 抛弃收到的数据
            ReferenceCountUtil.release(msg);//如果不是继承的SimpleChannel...则需要自行释放msg
        }
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	System.out.println("PC "+ctx.channel().remoteAddress()+" connected!");
    	Map<String,Channel> map_temp = RunPcServer.getChMap();
    	synchronized(map_temp) {
    		map_temp.put(ctx.channel().remoteAddress().toString(), ctx.channel());
    	}
    	
    	ctx.writeAndFlush(Unpooled.copiedBuffer("Connected!",CharsetUtil.UTF_8));
        ctx.fireChannelActive();
    }
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		Map<String,Channel> map_temp = RunPcServer.getChMap();

		map_temp.remove(ctx.channel().toString());
		System.out.println("PC "+ctx.channel().remoteAddress().toString()+" disconnected!");
		ctx.fireChannelInactive();
	}
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }
    
}

