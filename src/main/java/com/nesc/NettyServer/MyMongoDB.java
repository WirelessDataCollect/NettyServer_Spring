package com.nesc.NettyServer;

import org.bson.Document;

import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
/**
* 
* MongoDB数据库类
*
* @author  nesc528
* @Date    2018-9-7
* @version 0.0.1
*/
public class MyMongoDB{
	public MongoCollection<Document> collection;
	protected MongoClient mongoClient;
	protected MongoDatabase mongoDatabase;
	private String colName = "data";
	private String dbName = "udp";
	
	/**
	 * MongoDB数据库的集合选择方法
	 * @param colName
	 */
	public void setColName(String colName) {
		this.colName = colName;
		try {
			 collection = mongoDatabase.getCollection(colName);
			 System.out.printf("Connect to col:%s successfully\n",colName);			
		}catch(Exception e) {
			  mongoClient = null;
			  mongoDatabase = null;
			  System.err.println( e.getClass().getName() + ": " + e.getMessage());			
		}
	}
	/**
	* MongoDB数据库类的设置函数
	*
	* @param dbName 数据库名称
	* @return 无
	* @throws 无
	*/
	public void setDbName(String dbName) {
		this.dbName = dbName;
		try{
			/* 连接到 mongodb 服务*/
			 mongoClient = MongoClients.create();
			   
			     /* 连接到数据库*/
			 mongoDatabase = mongoClient.getDatabase(this.dbName);  
			 System.out.printf("Connect to db:%s successfully\n",this.dbName);    
		  }catch(Exception e){
			  mongoDatabase = null;
			  mongoClient.close();
			  System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		  }    	
	}
	/**
	 * 获取对象连接的数据库
	 * @return dbName：本次操作
	 */
	public String getdbName() {
		return dbName;
	}
	/**
	 * 获取对象操作的集合
	 * @return colName：本次操作集合名称
	 */
	public String getColname() {
		return colName;
	}
}
