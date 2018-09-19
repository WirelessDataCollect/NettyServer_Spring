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
    public static void main(String[] args) { 
//    	TestTools test = new TestTools();
//    	test.start();
    	ApplicationContext pcServerContext = new ClassPathXmlApplicationContext("beans.xml");
    	RunPcServer pc_server = (RunPcServer)pcServerContext.getBean("runPcServer");
    	pc_server.start();
    	RunDeviceServer device_server = new RunDeviceServer("Device-Thread");
    	device_server.start();   	
    }
}