/**
 * 
 */
package com.ccms.util.lunece.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.util.AttributeSource;

import com.ccms.util.lunece.util.Synonym;

/**
 * @author 
 *
 */
public class HTokenFilter extends TokenFilter {

	private Synonym  synonym ;
	private Stack<String> synonymsStack;
	private AttributeSource.State currentState;
	private CharTermAttribute charTermAttribute = null;
	private PositionIncrementAttribute positionIncrementAttribute = null;
	private HAnalyzer analyzer;
	
	protected HTokenFilter(TokenStream input , Synonym s , HAnalyzer a) {
		super(input);
		this.synonym = s;
		synonymsStack = new Stack<String>();
		charTermAttribute = this.addAttribute(CharTermAttribute.class);
		positionIncrementAttribute = this.addAttribute(PositionIncrementAttribute.class);
		analyzer = a;
	}

	@Override
	public boolean incrementToken() throws IOException {
		if(synonymsStack.size() > 0) {
			//当前属性状态还原
			restoreState(currentState); 
			//清空属性当前位
			charTermAttribute.setEmpty(); 
			//同义词、近义词填充位
			charTermAttribute.append(synonymsStack.pop());
			//设置增量值为0
			positionIncrementAttribute.setPositionIncrement(0); 
			
			return true;
		}
		if(!this.input.incrementToken()) return false;
		if(this.pushSynonym(charTermAttribute.toString())) {
			currentState = captureState();
		}
		return true;
	}
	
	private boolean pushSynonym(String word) {
		
		List<Set<String>> ls = synonym.getSysnonyms(word);
		if(ls != null) {
			HashMap<String , Float> hm = new HashMap<String , Float> ();
			for (Set<String> arr : ls) {
				if (arr != null) {
					for (String str : arr) {
						hm.put(str, 1.0f);
						synonymsStack.push(str);
					}
				} else {
					hm.put(word, 200.0f);
				}
			}
			hm.put(word, 200.0f);
			analyzer.getSynDic().setSynonym(hm); //装载同义词、近义词
			return true;		
		}		
		return false;
	}
}
