package com.nesc.NettyServer;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;;

/**
* 
* 基于WiFi的物联网数据采集服务器程序
*
* @author  nesc528
* @Date    2018-9-7
* @version 0.0.1
*/
public class App{
	public static ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
    public static void main(String[] args) { 
    	TestTools test = new TestTools();
    	test.start();	
    	RunPcServer pc_server = (RunPcServer)context.getBean("runPcServer");
    	pc_server.start();
    	RunDeviceServer device_server = (RunDeviceServer)context.getBean("runDeviceServer");
    	device_server.start();   	
    }
	/**
	 * 获取bean的一个应用上下文
	 * @return context
	 */
	public static ApplicationContext getApplicationContext() {
		return context;
	}
}