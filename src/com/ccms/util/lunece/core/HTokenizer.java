/**
 * 
 */
package com.ccms.util.lunece.core;

import java.io.Reader;

import com.ccms.util.lunece.analyzer.lucene.IKTokenizer;

/**
 * @author 
 *
 */
public class HTokenizer extends IKTokenizer {

	/**
	 * @param in
	 * @param useSmart
	 */
	public HTokenizer(Reader in, boolean useSmart) {
		super(in, useSmart);
	}

}
