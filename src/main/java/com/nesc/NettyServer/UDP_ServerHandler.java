package com.nesc.NettyServer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

/**
* 
* UDP服务器的输入处理器函数.如果给多个Pipeline,需要给类添加@Sharable
*
* @author  nesc528
* @Date    2018-9-7
* @version 0.0.1
*/
public class UDP_ServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {
	private DataProcessor processor;
	UDP_ServerHandler(){
		processor =(DataProcessor) App.getApplicationContext().getBean("dataProcessor");
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {  //channelRead0在退出前，后面的不会打断
		RunDeviceServer.incPacksNum();  //每次进入数据接受，都要更新包裹数目
		//如果数字超过了127,则会变成负数为了解决这个问题需要用getUnsignedByte
		ByteBuf temp = msg.content();
		DeviceServerTools.send2Pc(temp);
		processor.dataProcess(temp);

	}
	/**
	 * 当channel建立的时候回调（不面向连接，也无法返回数据回去），不同于TCP
	 * 
	 * 在UDPbind的时候，服务器也不会进入channelActive。
	 * 
	 * channelActive是自行创建的时候，进入的。
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(
                "Device UDP channel " + ctx.channel().toString() + " created");
    }
    /**
     * 当Netty由于IO错误或者处理器在处理事件时抛出异常时调用
     */	
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        ctx.close();
        cause.printStackTrace();
    }        
}