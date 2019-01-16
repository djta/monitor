/**
 * 
 */
package com.ccms.util.lunece.util;

import java.util.Map;

/**
 * @author 
 *
 */
public class SynonymDic {

	public SynonymDic() {
	}
	
	private Map<String , Float>  synonym ;

	public Map<String , Float> getSynonym() {
		return synonym;
	}

	public void setSynonym(Map<String , Float> synonym) {
		if(this.synonym != null ){
			this.synonym.putAll(synonym);
		}else{
			this.synonym = synonym;
		}
		
	}
	
}
