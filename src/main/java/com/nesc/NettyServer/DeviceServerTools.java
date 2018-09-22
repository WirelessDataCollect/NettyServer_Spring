package com.nesc.NettyServer;

import java.util.Iterator;
import java.util.Map;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

/**
* 
* 设备服务器工具，包括转发数据给PC上位机。在此之前，需要有TCP连接，即MAP中有上位机同服务器连接的Channel。
*
* @author  nesc528
* @Date    2018-9-7
* @version 0.0.1
*/
public class DeviceServerTools{
	/**
	 * 转发设备信息至PC端上位机
	 * @param temp
	 */
	protected static void send2Pc(ByteBuf temp) {   //这里需要是静态的，非静态依赖对象
		synchronized(RunPcServer.getChMap()) {
			for(Iterator<Map.Entry<String,Channel>> item = RunPcServer.getChMap().entrySet().iterator();item.hasNext();) {
				Map.Entry<String,Channel> entry = item.next();
				entry.getValue().pipeline().writeAndFlush(temp.copy());
			}	
		}
	}
}
