package com.nesc.NettyServer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
* 
* TCP服务器的输入处理器函数
*
* @author  nesc528
* @Date    2018-9-7
* @version 0.0.1
*/
public class TCP_ServerHandler extends ChannelInboundHandlerAdapter {
	private DataProcessor processor;
	TCP_ServerHandler(){
		processor =(DataProcessor) App.context.getBean("dataProcessor");//获取一个数据处理器
	}
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
        	RunDeviceServer.incPacksNum();//1秒钟内的包++
    		ByteBuf temp = (ByteBuf)msg;
    		DeviceServerTools.send2Pc(temp.copy());
    		processor.dataProcess(temp);
        } finally {
            // 抛弃收到的数据
            ReferenceCountUtil.release(msg);//如果不是继承的SimpleChannel...则需要自行释放msg
        }
    }
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {   //1
        System.out.println(
                "Device TCP channel " + ctx.channel().toString() + " created");
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }
}