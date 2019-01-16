package com.ccms.quartz.pub.dialect;

public class SQLServerDialect extends Dialect {

	static int getAfterSelectInsertPoint(String sql) {
		int selectIndex = sql.toLowerCase().indexOf( "select" );
		final int selectDistinctIndex = sql.toLowerCase().indexOf( "select distinct" );
		return selectIndex + ( selectDistinctIndex == selectIndex ? 15 : 6 );
	}

	public String getLimitString(String querySelect, int offset, int limit) {
		//if (offset>0) throw new UnsupportedOperationException("sql server has no offset");
		if (offset>0){
			return new StringBuffer( querySelect.length()+100 )
			.append(querySelect)
			.append(" OFFSET ( ")
			.append(offset)
			.append(" ) ROW FETCH NEXT ")
			.append(limit)
			.append(" rows only ")
			.toString();
		}else{
			return new StringBuffer( querySelect.length()+8 )
			.append(querySelect)
			.insert( getAfterSelectInsertPoint(querySelect), " top " + limit )
			.toString();
		}
	}
}
