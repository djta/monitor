/**
 * 
 */
package com.ccms.util.lunece.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ccms.context.InitializerServlet;
import com.ccms.util.lunece.analyzer.cfg.Configuration;

 

/**
 * @author 
 *
 */
public final class HSynonym extends Synonym {

	/**
	 * 加载物理文件，形成Map
	 */
	private static Map<String,String[]> dicMap;
	/**
	 * 构建code对应的Map树
	 */
	private static Map<String,Set<String>> SynonymWordMap;
	/**
	 * code容器
	 */
	private static Set<String> codeSet;
	
	/**
	 * 获取初始化同义词Map
	 * @return
	 */
	public static Map<String, String[]> getSynonymsMap() {
		return dicMap;
	}

	/**
	 * 获取构建够的同义、近义词HashMap
	 * @return
	 */
	public static Map<String, Set<String>> getWordMap() {
		return SynonymWordMap;
	}
	
	public HSynonym() {
		if (dicMap == null) {
			dicMap = new HashMap<String, String[]>();
			SynonymWordMap = new HashMap<String,Set<String>>();
			codeSet = new HashSet<String>();
			try {
				setAutoLoad(false);
				//loadExtSynoymDic();
				loadSynonymDic();
				initSynonymDic();
			} catch (IOException e) {
				//LogUtil.getLogger().error(e.getMessage());
			}		
		}
	}
	
	/* 
	 * 获取近义词、同义词
	 * 
	 */
	@Override	
	public List<Set<String>> getSysnonyms(String word) {
		List<Set<String>> retList = new ArrayList<Set<String>>();
		String[] codeArr = getCodeForWord(word);
		if (codeArr != null){
			for (String code : codeArr) {
				retList.add(getWordMapForWord(code));
			}
		}
		return retList;
	}

	/**
	 * 在内存中添加同义词，但不写入物理文件
	 * @param key
	 * @param val
	 * @return
	 */
	@Deprecated
	public boolean  addSynonyms(String key ,String []val) {
		if (dicMap != null) {
			dicMap.put(key, val);
			return true;
		}else
			return false;
	}
	
	/**
	 * 删除内容中的同义词、近义词，但不删除物理文件
	 * @param key
	 * @return
	 */
	@Deprecated
	public boolean removeSynonyms(String key) {
		if (dicMap != null) {
			dicMap.remove(key);
			return true;
		}else
			return false;
	}
	
	/**
	 * 同义词加载
	 * @throws IOException
	 */
	private void loadSynonymDic() throws IOException {
		//LogUtil.getLogger().info("开始加载同义词词典"); 
		System.out.println("load synonym_all.dic");
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(Configuration.synonymDicPath);
		try {
			if(is == null) throw  new RuntimeException("读取"+Configuration.synonymDicPath+"失败");
			InputStreamReader isr = new InputStreamReader(is , "UTF-8");
			BufferedReader br = new BufferedReader(isr , 1024);
			String  sWord = null;
			while((sWord = br.readLine()) != null) {
				String [] tmpArr = StringUtil.splitEx(sWord, " ");
				String [] valueArr = new String[tmpArr.length - 2];
				for(int i = 2 ; i<tmpArr.length; i++) {
					valueArr[i - 2] = tmpArr[i];
					if(tmpArr[i].indexOf("@") > 0) continue;
					codeSet.add(tmpArr[i]);
				}
				dicMap.put(tmpArr[0], valueArr );
			}
			//this.setWordMap(dicMap , codeSet , getAutoLoad()); //一次启动加载
			br.close();
			isr.close();
			is.close();
			this.loadExtSynoymDic();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static Set<String> arrayToSet(String []args){
		Set<String> set = new HashSet<String>(args.length);
		for (String string : args) {
			set.add(string);
		}
		return set;
	}
	
	/**
	 * 加载扩展词典
	 * @throws IOException
	 */
	public void loadExtSynoymDic() throws IOException{
		
		System.out.println("load synonym_ext.dic");
		
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(Configuration.synoymExtDictPath);
		
		if(is == null){
			return;
		}
		InputStreamReader isr = new InputStreamReader(is , "UTF-8");
		BufferedReader br = new BufferedReader(isr , 1024);
		String  sWord = null;
		while((sWord = br.readLine()) != null) {
			String [] tmpArr = StringUtil.splitEx(sWord, ":");
			String [] valueArr = tmpArr[2].split(",");
			if(dicMap.containsKey(tmpArr[0])){ // 如果缓存中存在，就追加到后面
				String [] words = dicMap.get(tmpArr[0]);
				Set<String> oldVal = arrayToSet(words);
				Set<String> newVal = arrayToSet(valueArr);
				oldVal.addAll(newVal);
				dicMap.put(tmpArr[0], oldVal.toArray(new String[0]));
			} else 
				dicMap.put(tmpArr[0], valueArr); 
		}
		br.close();
		isr.close();
		is.close();
		
		
	}
	
	/**
	 * 初始化SynonymWordMap；
	 *
	 */
	public void initSynonymDic() {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(Configuration.synonymDicSetPath);
		if(is == null) throw  new RuntimeException("读取"+Configuration.synonymDicSetPath+"失败");
		try {
		InputStreamReader isr = new InputStreamReader(is , "UTF-8");
		BufferedReader br = new BufferedReader(isr , 1024);
		String  sWord = null;
		while((sWord = br.readLine()) != null) {
			String [] tmpArr = StringUtil.splitEx(sWord, " ");
			Set<String> value = new HashSet<String>();
			String key = null;
			for(int i = 0 ; i<tmpArr.length; i++) {
				if(i == 0) {
					key = tmpArr[i];
				}else{
					value.add(tmpArr[i]);
				}
			}
			SynonymWordMap.put(key, value);
		}

		br.close();
		isr.close();
		is.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 分两种次用ArryList：分别是“=”、“#”、“@”三种。
	 * “=”代表“相等”、“同义”；
	 * “#”代表“不等”、“同类”，属于相关词语；
	 * “@”代表独立词，它在词典中既没有同义词，也没有相关词
	 * 在没有同义词生成文件时候使用
	 * @param map
	 * @param set
	 * @return
	 */
	@SuppressWarnings("unused") //配置同义词库时候开启使用
	private Map<String, Set<String>> setWordMap(Map<String, String[]> map, Set<String> set, boolean isAutoLoad) {
		if (!isAutoLoad)
			return null;
		Set<String> v;
		Iterator<String> it;
		OutputStreamWriter osw = null;
		try {
			String path = InitializerServlet.getContext().getRealPath("/");
			osw = new OutputStreamWriter(new FileOutputStream(FileUtil.normalizePath(path) + "/WEB-INF/classes" + Configuration.synonymDicSetPath,false),"UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}   
		BufferedWriter bw = new BufferedWriter(osw);
		for (String element : set) {
			it = map.keySet().iterator();
			v = new HashSet<String>();
			String tmpVal = "";
			while (it.hasNext()) {
				String tmpKey = it.next();
				String[] tmpValue = map.get(tmpKey);
				for (String arr : tmpValue) {
					if (element.equals(arr)) {
						v.add(tmpKey);
						tmpVal = tmpVal +" "+ tmpKey;
						break;
					}
				}
			}
			try {
				bw.write(element + tmpVal);
				bw.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			SynonymWordMap.put(element, v);
		}
		
		if (osw != null && osw != null ) {
			try {
				bw.close();
				osw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return SynonymWordMap;
	}
	

	/**
	 * 根据code查询对应的同义词组
	 * @param code
	 * @return
	 */
	public Set<String> getWordMapForWord(String code) {
		Set<String> set = new HashSet<String>();
		
		if (SynonymWordMap == null) {
			Iterator<String> it = dicMap.keySet().iterator();
			while (it.hasNext()) {
				String tmpKey = it.next();
				String[] tmpValue = (String[]) dicMap.get(tmpKey);
				for (String arr : tmpValue) {
					if (code.equals(arr)) {
						set.add(tmpKey);
						break;
					}
				}
			}
		}else{
			set = SynonymWordMap.get(code);
		}
		return set;
	}
	
	/**
	 * 根据单词查询对应code
	 * @param word
	 * @return
	 */
	public String[] getCodeForWord(String word) {
		return dicMap.get(word);
	}
}