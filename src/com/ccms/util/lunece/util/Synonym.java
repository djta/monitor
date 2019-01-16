/**
 * 
 */
package com.ccms.util.lunece.util;

import java.util.List;
import java.util.Set;



/**
 * @author 
 *
 */
public abstract class Synonym {
	
	
	private boolean autoLoad = true;
	
	/**
	 * 获取同义词、近义词
	 * @param name
	 * @return
	 */
	public Synonym() {
		
	}
	
	public abstract List<Set<String>> getSysnonyms(String name);
	
	public boolean getAutoLoad() {
		return autoLoad;
	}
	public void setAutoLoad(boolean autoLoad) {
		this.autoLoad = autoLoad;
	}
	
}
