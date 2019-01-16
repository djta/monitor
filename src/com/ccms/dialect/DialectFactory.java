package com.ccms.dialect;

public class DialectFactory {

	public static Dialect buildDialect(String databaseName) throws Throwable{
		if(databaseName.contains("db2")){
			return new DB2Dialect();
		}else if(databaseName.contains("postgresql")){
			return new PostgreSQLDialect();
		}else if(databaseName.contains("oracle")){
			return new OracleDialect();
		}else if(databaseName.contains("sql server")){
			return new SQLServerDialect();
		}else if(databaseName.contains("mysql")){
			return new MySQLDialect();
		}	
		throw new Throwable("没有匹配的数据库");
	}
}
