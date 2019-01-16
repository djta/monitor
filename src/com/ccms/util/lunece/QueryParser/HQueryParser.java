/**
 * 
 */
package com.ccms.util.lunece.QueryParser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.util.Version;

/**
 * @author HDH 
 *
 */
public class HQueryParser extends MultiFieldQueryParser  {

	public HQueryParser(Version matchVersion, String[] fields, Analyzer analyzer)
	{
		super(matchVersion, fields, analyzer);
	}
	
	static StringBuilder regex = new StringBuilder("(\"[^\"]+\"");
	
	static
	{
		regex.append("|《[^》]+》");
		regex.append("|<[^>]+>");
		regex.append("|\\([^\\)]+\\)");
		regex.append("|\'[^\']+\'");
		regex.append("|‘[^’]+’");
		regex.append("|（[^）]+）");
		regex.append("|“[^”]+”)");
	}

	@Override
	public Query parse(String search) throws ParseException 
	{					
		Pattern isbn =  Pattern.compile(regex.toString(),Pattern.CASE_INSENSITIVE);			
		Matcher matcher = isbn.matcher(search);
		List<String> strQ = null ;
		while(matcher.find())
		{
			if(strQ==null)
			{
				strQ = new ArrayList<String>();
			}
			strQ.add(matcher.group(0)); 
			if(strQ.size()>=3)
			{
				break ;
			}
		}
		if(strQ!=null && strQ.size()>0)
		{			
			super.setDefaultOperator(QueryParser.AND_OPERATOR);			
			BooleanQuery query = new BooleanQuery();
			for (int i = 0; i < strQ.size(); i++) 
			{
				query.add(super.parse(strQ.get(i)),Occur.SHOULD);
			}	
			return query ;			  
		}
		Query query = null;
		try {
			query = super.parse(search) ;
		} catch (ParseException e){ 
			search = search.replaceAll("[\\+\\-\\&\\|\\!\\(\\)\\{\\}\\[\\]\\^\\~\\*\\?\\:\\\\]","");
			query = super.parse(search);			
		}
		
		return query;
	}
}
