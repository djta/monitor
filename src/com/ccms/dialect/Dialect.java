package com.ccms.dialect;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class Dialect {

	private static final Log log = LogFactory.getLog( Dialect.class );

	protected Dialect() {
		log.info( "Using dialect: " + this );
	}

	public String getLimitString(String querySelect, boolean hasOffset) {
		throw new UnsupportedOperationException( "paged queries not supported" );
	}

	public String getLimitString(String querySelect, int offset, int limit) {
		return getLimitString( querySelect, offset>0 );
	}

}
