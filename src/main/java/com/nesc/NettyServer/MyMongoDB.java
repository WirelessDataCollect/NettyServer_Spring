package com.nesc.NettyServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

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
	}
	/**
	 * 获取对象连接的数据库
	 * @return dbName：本次操作
	 */
	public String getDbName() {
		return dbName;
	}
	/**
	 * 获取对象操作的集合
	 * @return colName：本次操作集合名称
	 */
	public String getColname() {
		return colName;
	}
	/**
	 * 准备操作一个db
	 * @param dbName 想要操作的数据库名称
	 * @return {@link MongoDatabase} 数据库
	 * @throws IllegalArgumentException
	 */
	public MongoDatabase getDatabase(String dbName) throws IllegalArgumentException{
		this.dbName = dbName;
		try {
			return this.mongoClient.getDatabase(this.dbName);
		}catch(IllegalArgumentException e) {
			throw e;
		}
	}
	/**
	 * 准备操作一个collection
	 * @param colName 想要操作的数据库集合名称
	 * @return {@link MongoCollection} 数据库集合
	 * @throws IllegalArgumentException
	 */
	public MongoCollection<Document> getCollection(String colName) throws IllegalArgumentException{
		this.colName = colName;
		try {
			return this.mongoDatabase.getCollection(this.colName);
		}catch(IllegalArgumentException e) {
			throw e;
		}
	}
	/**
	 * mongodb初始化函数，连接mongodb
	 */
	@PostConstruct   //初始化回调函数
	public void init() {
		/* 连接到 mongodb 服务*/
		try {
			mongoClient = MongoClients.create();
			mongoDatabase = mongoClient.getDatabase(dbName);
			collection = mongoDatabase.getCollection(colName);
			System.out.printf("Connected to db.col(%s.%s) successfully\n",dbName,colName);	
		}catch(Exception e) {
			System.out.println("MongoDB init unsuccessfully!");
		}
	}
	/**
	 * 断开和mongodb的连接
	 */
	@PreDestroy    //销毁回调函数
	public void destroy() {
		mongoClient.close();
	}
}
