/**
 * 
 */
package com.ccms.util.lunece.core;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;

import com.ccms.util.lunece.analyzer.lucene.IKTokenizer;
import com.ccms.util.lunece.util.HSynonym;
import com.ccms.util.lunece.util.Synonym;
import com.ccms.util.lunece.util.SynonymDic;



/**
 * 
 * @author 
 *
 */
public class HAnalyzer extends Analyzer {

	/**
	 * 同义词封装类
	 */
	private  Synonym sword;

	private SynonymDic synDic;
	/**
	 * 是否使用智能模式
	 */
	private  boolean isUseSmart;
	
	/**
	 * 是否使用同义词、近义词模式
	 */
	private  boolean isUseSynonym;
	
	/**
	 * 当前同义词典中是否有词
	 */
	private boolean currentState = true;
	
	
	//private ATokenFilter atf;
	
	/**
	 * 是否使用智能模式
	 * @param isUseSmart
	 */
	public HAnalyzer(boolean isUseSmart){
		this.sword = new HSynonym();
		synDic = new SynonymDic(); 
		this.isUseSmart = isUseSmart;
		this.isUseSynonym = true;
	}
	
	/**
	 * @param isUseSmart  是否使用智能模式
	 * @param isUseSynonym  是否使用同义词模式
	 */
	public HAnalyzer(boolean isUseSmart , boolean isUseSynonym) {
		this.isUseSmart = isUseSmart;
		this.isUseSynonym = isUseSynonym;
		if(isUseSynonym ) {
			this.sword = new HSynonym() ;
			synDic = new SynonymDic();
		}
	}
	
	/*@Override
	public TokenStream tokenStream(String fieldName, Reader reader) {
		if(isUseSynonym) {
			return  new ATokenFilter(new ATokenizer(reader , isUseSmart) , sword ,this);
		}
		else
			return  new IKTokenizer(reader , isUseSmart);
	}*/
	
	@Override
	protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
		Tokenizer tokenizer = null ;
		if(isUseSynonym) {
			tokenizer = new HTokenizer(reader , isUseSmart);
			return new TokenStreamComponents(tokenizer,new HTokenFilter(tokenizer, sword ,this));
		}
		else{
			tokenizer =  new IKTokenizer(reader , isUseSmart);
			return new TokenStreamComponents(tokenizer);
		}
        
	}	

	public SynonymDic getSynDic() {
		setCurrentState(true);
		return synDic;
	}

	public void setSynDic(SynonymDic synDic) {
		this.synDic = synDic;
	}

	public boolean getCurrentState() {
		return currentState;
	}

	public void setCurrentState(boolean currentState) {
		this.currentState = currentState;
	}
	
	

}
