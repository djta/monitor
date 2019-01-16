package com.ccms.core.foctory;


import java.util.HashMap;
import java.util.Map;

import dinamica.Db;
import dinamica.Recordset;
import dinamica.StringUtil;
import dinamica.TemplateEngine;

public class FormOutput
{

	public Db db = null;
	
	public Db getDb() {
		return db;
	}
	
	public FormOutput(){
		
	}
	
	public FormOutput(Db db){
		this.db = db;
	}
    /**
     * @param db
     * @param form 
     * @param tmp 模板
     * @param flag 1：form 2：filter
     * @param locale 1：cn 2：en
     * @throws Throwable
     */
    public String printPage(FormBean form, TemplateBean tmp, int flag, int locale) throws Throwable
    {

        //加载模版,替换掉item标签
        String tCheckbox = StringUtil.replace(flag==1?tmp.gettCheckbox():tmp.gettCheckboxFilter(),"_${fld:form_item_id}","");
        String tRadiobox = StringUtil.replace(flag==1?tmp.gettRadiobox():tmp.gettRadioboxFilter(),"_${fld:form_item_id}","");
        String tText = StringUtil.replace(flag==1?tmp.gettText():tmp.gettTextFilter(),"_${fld:form_item_id}","");
        String tCombo = StringUtil.replace(flag==1?tmp.gettCombo():tmp.gettComboFilter(),"_${fld:form_item_id}","");
        String tDate = StringUtil.replace(flag==1?tmp.gettDate():tmp.gettDateFilter(),"_${fld:form_item_id}","");
        String tDateTime = StringUtil.replace(flag==1?tmp.gettDateTime():tmp.gettDateTimeFilter(),"_${fld:form_item_id}","");
        String tHidden = StringUtil.replace(flag==1?tmp.gettHidden():tmp.gettHiddenFilter(),"_${fld:form_item_id}","");
        String tTextReadonly = StringUtil.replace(flag==1?tmp.gettTextReadonly():tmp.gettTextReadonlyFilter(),"_${fld:form_item_id}","");
        String tTextShowonly = StringUtil.replace(flag==1?tmp.gettTextShowonly():tmp.gettTextShowonlyFilter(),"_${fld:form_item_id}","");
        String tTextarea = StringUtil.replace(flag==1?tmp.gettTextarea():tmp.gettTextareaFilter(),"_${fld:form_item_id}","");
        String tPickup = StringUtil.replace(flag==1?tmp.gettPickup():tmp.gettPickupFilter(),"_${fld:form_item_id}","");
        String tPlugin = StringUtil.replace(flag==1?tmp.gettPlugin():tmp.gettPluginFilter(),"_${fld:form_item_id}","");
		String tEmpty = StringUtil.replace(flag==1?tmp.gettEmpty():tmp.gettEmptyFilter(),"_${fld:form_item_id}","");

		String tRow  = flag==1?tmp.gettRow():tmp.gettRowFilter();
        String tItem  = flag==1?tmp.gettItem():tmp.gettItemFilter();
        String tItemNone  = flag==1?tmp.gettItemNone():tmp.gettItemNoneFilter();
		
        String strDomain = locale==1?tmp.getStrDomainCn():tmp.getStrDomainEn();
		String strFkField = tmp.getStrFkField();
		String strFkData = locale==1?tmp.getStrFkDataCn():tmp.getStrFkDataEn();
		
		strDomain = StringUtil.replace(strDomain, "${subject_id}", form.getSubject_id().toString());
		strFkData = StringUtil.replace(strFkData, "${subject_id}", form.getSubject_id().toString());
        
        String queryItem = tmp.getQueryItem();
        String queryField = flag==1?tmp.getFormQueryFieldSql():tmp.getFilterQueryFieldSql();

        TemplateEngine teItem = new TemplateEngine(null, null, queryItem);
        teItem.replace(form.getRsForm(),"");
        queryItem = teItem.toString();
        
        TemplateEngine teField = new TemplateEngine(null, null, queryField);
        teField.replace(form.getRsForm(),"");
        queryField = teField.toString();
        
        Recordset rsItem = db.get(queryItem);

        StringBuffer hgrid_item = new StringBuffer();
        rsItem.top();
        while (rsItem.next()){
        	//判断中英文
        	rsItem.setValue("item_name", locale==1?rsItem.getValue("item_name_cn"):rsItem.getValue("item_name_en"));
        	
            TemplateEngine tField = new TemplateEngine(null, null, queryField);
            tField.replace(rsItem,"");
            Recordset rsField = db.get(tField.toString());

            if(rsField.getRecordCount()==0 && (rsItem.getString("linkage_document_id")==null || flag==2))//处理同显页面,flag!=1则为查询界面
                continue;            	
            
            //单独处理hidden
    		StringBuffer hiddenBuf = new StringBuffer();
            rsField.top();
    		while(rsField.next()){
    			if(rsField.getString("show_type") != null && rsField.getString("show_type").equals("9")){
    				hiddenBuf.append(getText(rsField,tHidden));
    				rsField.delete(rsField.getRecordNumber());    				
    			}
    		}
    		
            if(rsField.getRecordCount()==0){	//全部为hidden
                TemplateEngine t1 = new TemplateEngine(null,null, rsItem.getInt("item_id")==0?tItemNone:tItem);
                t1.replace(rsItem,"");                
                hgrid_item.append(StringUtil.replace(t1.toString(),"${items}", hiddenBuf.toString()));
                    continue;            	
            }

            //取每行列数
            int cols = 1;
            if(rsItem.getString("col_num")==null)
                cols = flag==1?form.getCol_num_edit():form.getCol_num_filter();
            else
                cols = Integer.parseInt(rsItem.getString("col_num"));
    
            if(cols==0)
                cols = 1;

            //定义一个保存跨行跨列状态的数组
            int maxShowRow = 200;
            int  span[][] = new int[maxShowRow+1][cols+1];
            for (int i=0; i<span.length; i++){
                for (int j=0; j<span[i].length; j++){
                    span[i][j] = 0; //初始值
                }
            }
    
            Integer iShowType = 0;
            
            int iRow=0;		//当前行
            int iCol=0;		//当前列
            int iDelays=0;    //补占的列数
            
            //hgrid body
            StringBuffer hgrid = new StringBuffer();
            if(rsField.getRecordCount() > 0)
            {
                for (int k=0;k<rsField.getRecordCount()+iDelays;k=k+cols)		//k为总的单元数.
                {
                    iRow++;		//新的一行
                    StringBuilder colsBuf = new StringBuilder();
                    for (int i=0;i<cols;i++)
                    {
                        iCol++;
    
                        if(getSpan(span,iRow,iCol)>=1){  //本位置被占
                            iDelays++;
                            continue;
                        }
    
                        if (k+i==rsField.getRecordCount()+iDelays) {	//该换行了,后面的补td
                            for (int j=i;j<cols;j++) {
                                colsBuf.append(tEmpty);
                            }
                            break;
                        }
        
                        rsField.setRecordNumber(k+i-iDelays);
    
                        //判断中英文
                        rsField.setValue("field_name", locale==1?rsField.getValue("field_name_cn"):rsField.getValue("field_name_en"));
                        
                        //设置跨行跨列数据
                        setSpan(span,iRow,iCol,Integer.parseInt(rsField.getString("rowspan")),Integer.parseInt(rsField.getString("colspan")));
                        
                        if(rsField.getString("show_type") != null)
                            iShowType = rsField.getInteger("show_type");
                        else
                            iShowType = 0;  /*默认文本框 */
                            
                         switch (iShowType.intValue()) {
                            case 0:  	//文本框
                                colsBuf.append(getText(rsField,tText));
                                break;
                            case 1:  	//下接框
                                colsBuf.append(getCombo(rsField,tCombo,strDomain,strFkField,strFkData));
                                break;
                            case 2:  	//复选框
                                colsBuf.append(getCheckbox(rsField,tCheckbox,strDomain));
                                break;
                            case 3:  	//多选一
                                colsBuf.append(getRadio(rsField,tRadiobox,strDomain,strFkField,strFkData));
                                break;
                            case 4:  	//只读
                                colsBuf.append(getText(rsField,tTextReadonly));
                                break;
                            case 5:  	//日期
                                colsBuf.append(getText(rsField,tDate));
                                break;
                            case 6:  	//文本域
                                colsBuf.append(getText(rsField,tTextarea));
                                break;
                            case 7:  	//选取框
                                colsBuf.append(getText(rsField,tPickup));
                                break;
                            case 8:  	//插件(在字段一级指定)
                                colsBuf.append(getText(rsField,getText(rsField,tPlugin)));
                                break;
                            case 9:  	//HIDDEN
                                colsBuf.append(getText(rsField,tHidden));
                                break;
                            case 10:  	//日期时间
                                colsBuf.append(getText(rsField,tDateTime));
                                break;
                            case 11:  	//仅显示
                                colsBuf.append(getText(rsField,getText(rsField,tTextShowonly)));
                                break;
                            default: 
                                //未指定控件类型
                                colsBuf.append(getText(rsField,tText));
                            break;
                        }
                    }
                    iCol = 0;
                    hgrid.append(StringUtil.replace(tRow,"${cols}", colsBuf.toString()));
                }
            }
            //如果没有选择分类则不需要分类
            TemplateEngine t1 = new TemplateEngine(null, null, rsItem.getInt("item_id")==0?tItemNone:tItem);
            t1.replace(rsItem,"");
            if(hgrid.length() > 0){
            	hgrid_item.append(StringUtil.replace(t1.toString(),"${items}", hgrid.toString()));
            }
            //把hidden拼上来
            hgrid_item.append(hiddenBuf.toString());
        }
        return StringUtil.replace(hgrid_item.toString(),"${DEF:","${def:");
    }

    /**
     * @param db
     * @param form 
     * @param tmp 模板
     * @param flag 1：form 2：filter
     * @param locale 1：cn 2：en
     * @throws Throwable
     */
    public Map<Integer, FormItemBean> printFormItemPage(FormBean form, TemplateBean tmp, int flag, int locale) throws Throwable
    {
    	Map<Integer, FormItemBean> itemMap = new HashMap<Integer, FormItemBean>();
        //加载模版
        String tCheckbox = flag==1?tmp.gettCheckbox():tmp.gettCheckboxFilter();
        String tRadiobox = flag==1?tmp.gettRadiobox():tmp.gettRadioboxFilter();
        String tText = flag==1?tmp.gettText():tmp.gettTextFilter();
        String tCombo = flag==1?tmp.gettCombo():tmp.gettComboFilter();
        String tDate = flag==1?tmp.gettDate():tmp.gettDateFilter();
        String tDateTime = flag==1?tmp.gettDateTime():tmp.gettDateTimeFilter();
        String tHidden = flag==1?tmp.gettHidden():tmp.gettHiddenFilter();
        String tTextReadonly = flag==1?tmp.gettTextReadonly():tmp.gettTextReadonlyFilter();
        String tTextShowonly = flag==1?tmp.gettTextShowonly():tmp.gettTextShowonlyFilter();
        String tTextarea = flag==1?tmp.gettTextarea():tmp.gettTextareaFilter();
        String tPickup = flag==1?tmp.gettPickup():tmp.gettPickupFilter();
        String tPlugin = flag==1?tmp.gettPlugin():tmp.gettPluginFilter();
		String tEmpty = flag==1?tmp.gettEmpty():tmp.gettEmptyFilter();

		String tRow  = flag==1?tmp.gettRow():tmp.gettRowFilter();
        String tItem  = flag==1?tmp.gettItem():tmp.gettItemFilter();
		
        String strDomain = locale==1?tmp.getStrDomainCn():tmp.getStrDomainEn();
		String strFkField = tmp.getStrFkField();
		String strFkData = locale==1?tmp.getStrFkDataCn():tmp.getStrFkDataEn();
		
		strDomain = StringUtil.replace(strDomain, "${subject_id}", form.getSubject_id().toString());
		strFkData = StringUtil.replace(strFkData, "${subject_id}", form.getSubject_id().toString());
        
        String queryItem = tmp.getQueryItem();
        String queryField = flag==1?tmp.getFormQueryFieldSql():tmp.getFilterQueryFieldSql();

        TemplateEngine teItem = new TemplateEngine(null, null, queryItem);
        teItem.replace(form.getRsForm(),"");
        queryItem = teItem.toString();
        
        TemplateEngine teField = new TemplateEngine(null, null, queryField);
        teField.replace(form.getRsForm(),"");
        queryField = teField.toString();
        
        Recordset rsItem = db.get(queryItem);

        StringBuffer hgrid_item = new StringBuffer(1024);
        rsItem.top();
        while (rsItem.next()){
        	//如果为默认栏,则不缓存.
        	if(rsItem.getString("item_id").equals("0")){
        		continue;
        	}
        	String strItemId = rsItem.getString("item_id");
        	
        	//判断中英文
        	rsItem.setValue("item_name", locale==1?rsItem.getValue("item_name_cn"):rsItem.getValue("item_name_en"));
        	
            TemplateEngine tField = new TemplateEngine(null, null, queryField);
            tField.replace(rsItem,"");
            Recordset rsField = db.get(tField.toString());

            if(rsField.getRecordCount()==0 && (rsItem.getString("linkage_document_id")==null || flag==2))//处理同显页面,flag!=1则为查询界面
                continue;            	
            
            //单独处理hidden
    		StringBuffer hiddenBuf = new StringBuffer();
            rsField.top();
    		while(rsField.next()){
    			if(rsField.getString("show_type") != null && rsField.getString("show_type").equals("9")){
    				hiddenBuf.append(getText(rsField,tHidden));
    				rsField.delete(rsField.getRecordNumber());    				
    			}
    		}
    		
            if(rsField.getRecordCount()==0){	//全部为hidden
                TemplateEngine t1 = new TemplateEngine(null,null, tItem);
                t1.replace(rsItem,"");                
                hgrid_item.append(StringUtil.replace(t1.toString(),"${items}", hiddenBuf.toString()));
                    continue;            	
            }

            //取每行列数
            int cols = 1;
            if(rsItem.getString("col_num")==null)
                cols = flag==1?form.getCol_num_edit():form.getCol_num_filter();
            else
                cols = Integer.parseInt(rsItem.getString("col_num"));
    
            if(cols==0)
                cols = 1;

            //定义一个保存跨行跨列状态的数组
            int maxShowRow = 200;
            int  span[][] = new int[maxShowRow+1][cols+1];
            for (int i=0; i<span.length; i++){
                for (int j=0; j<span[i].length; j++){
                    span[i][j] = 0; //初始值
                }
            }
    
            Integer iShowType = 0;
            
            int iRow=0;		//当前行
            int iCol=0;		//当前列
            int iDelays=0;    //补占的列数
            
            //hgrid body
            StringBuffer hgrid = new StringBuffer();
            if(rsField.getRecordCount() > 0)
            {
                for (int k=0;k<rsField.getRecordCount()+iDelays;k=k+cols)		//k为总的单元数.
                {
                    iRow++;		//新的一行
                    StringBuilder colsBuf = new StringBuilder();
                    for (int i=0;i<cols;i++)
                    {
                        iCol++;
    
                        if(getSpan(span,iRow,iCol)>=1){  //本位置被占
                            iDelays++;
                            continue;
                        }
    
                        if (k+i==rsField.getRecordCount()+iDelays) {	//该换行了,后面的补td
                            for (int j=i;j<cols;j++) {
                                colsBuf.append(tEmpty);
                            }
                            break;
                        }
        
                        rsField.setRecordNumber(k+i-iDelays);
    
                        //判断中英文
                        rsField.setValue("field_name", locale==1?rsField.getValue("field_name_cn"):rsField.getValue("field_name_en"));
                        
                        //设置跨行跨列数据
                        setSpan(span,iRow,iCol,Integer.parseInt(rsField.getString("rowspan")),Integer.parseInt(rsField.getString("colspan")));
                        
                        if(rsField.getString("show_type") != null)
                            iShowType = rsField.getInteger("show_type");
                        else
                            iShowType = 0;  /*默认文本框 */
                            
                         switch (iShowType.intValue()) {
                            case 0:  	//文本框
                                colsBuf.append(getText(rsField,tText));
                                break;
                            case 1:  	//下接框
                                colsBuf.append(getCombo(rsField,tCombo,strDomain,strFkField,strFkData,strItemId));
                                break;
                            case 2:  	//复选框
                                colsBuf.append(getCheckbox(rsField,tCheckbox,strDomain,strItemId));
                                break;
                            case 3:  	//多选一
                                colsBuf.append(getRadio(rsField,tRadiobox,strDomain,strFkField,strFkData,strItemId));
                                break;
                            case 4:  	//只读
                                colsBuf.append(getText(rsField,tTextReadonly));
                                break;
                            case 5:  	//日期
                                colsBuf.append(getText(rsField,tDate));
                                break;
                            case 6:  	//文本域
                                colsBuf.append(getText(rsField,tTextarea));
                                break;
                            case 7:  	//选取框
                                colsBuf.append(getText(rsField,tPickup));
                                break;
                            case 8:  	//插件(在字段一级指定)
                                colsBuf.append(getText(rsField,getText(rsField,tPlugin)));
                                break;
                            case 9:  	//HIDDEN
                                colsBuf.append(getText(rsField,tHidden));
                                break;
                            case 10:  	//日期时间
                                colsBuf.append(getText(rsField,tDateTime));
                                break;
                            case 11:  	//仅显示
                                colsBuf.append(getText(rsField,getText(rsField,tTextShowonly)));
                                break;
                            default: 
                                //未指定控件类型
                                colsBuf.append(getText(rsField,tText));
                            break;
                        }
                    }
                    iCol = 0;
                    hgrid.append(StringUtil.replace(tRow,"${cols}", colsBuf.toString()));
                }
            }
            TemplateEngine t1 = new TemplateEngine(null, null, tItem);
            t1.replace(rsItem,"");
            
            hgrid_item.append(StringUtil.replace(t1.toString(),"${items}", hgrid.toString()));
            //把hidden拼上来
            hgrid_item.append(hiddenBuf.toString());
            String strItemPage = StringUtil.replace(hgrid_item.toString(),"${DEF:","${def:");
            hgrid_item.delete(0, hgrid_item.length());
            FormItemBean bean = new FormItemBean(rsItem.getInteger("item_id"),strItemPage);
            itemMap.put(rsItem.getInteger("item_id"), bean);
        }
        return itemMap;
    }

    public String getCombo (Recordset rs,String te,String domain,String strFkField,String strFkData) throws Throwable
    {
    	return getCombo(rs,te,domain,strFkField,strFkData,null);
    }
    public String getCheckbox (Recordset rs,String te,String domain) throws Throwable
    {
    	return getCheckbox(rs,te,domain,null);
    }

    public String getRadio (Recordset rs,String te,String domain,String strFkField,String strFkData) throws Throwable
    {
    	return getRadio(rs,te,domain,strFkField,strFkData,null);
    }

    public String getText (Recordset rs,String te) throws Throwable
    {
        TemplateEngine t = new TemplateEngine(null, null, te);
        t.replace(rs,"");
        return t.toString();
    }

    public String getCombo (Recordset rs,String te,String domain,String strFkField,String strFkData,String item) throws Throwable
    {
        //取域值
        TemplateEngine t1 = new TemplateEngine(null, null, domain);
        t1.replace(rs,"");      
        Recordset rsDomain = db.get(t1.toString());
        
        TemplateEngine t = new TemplateEngine(null, null, te);
        t.replace(rs,"");

		if(rsDomain.getRecordCount()>0){
	        t.replace(rsDomain,"","rows");
	        
	        //设置默认值
	        rsDomain.top();
	        String strDefault = "";
	        while(rsDomain.next())
	        {
	            strDefault = rsDomain.getString("is_default");
	            if (strDefault!=null && strDefault.equals("1")){
	                t.setComboValue(rs.getString("form_field_code")+(item==null?"":"_"+item),rsDomain.getString("domain_value"));
	                t.replace(rsDomain,"");
	                break;
	            }
	        }
		}else{
			TemplateEngine t2 = new TemplateEngine(null, null, strFkField);
			t2.replace(rs,"");		
			Recordset rsFkFiled = db.get(t2.toString());
			if(rsFkFiled.next()){
				rsFkFiled.first();
				String schema = rsFkFiled.getString("schema_code");
				String table = rsFkFiled.getString("table_code");
				String field = rsFkFiled.getString("field_code");
				String field_alias = rsFkFiled.getString("field_alias");
				String fk_references = rsFkFiled.getString("fk_references");
				String fk_sql = rsFkFiled.getString("fk_sql");
				
				//外键配置数据不完整
				if(table==null || field==null || field_alias==null)
				{
					return t.toString();
				}
				if(fk_sql!=null && !"".equals(fk_sql)){
					fk_sql = StringUtil.replace(fk_sql, "${DEF", "${def");
					String table_alias = "(" + fk_sql + ") " + table;
					strFkData = StringUtil.replace(strFkData, "${schema}", "");
					strFkData = StringUtil.replace(strFkData, "${table}", table_alias);
				}else{
					strFkData = StringUtil.replace(strFkData, "${schema}", schema+".");
					strFkData = StringUtil.replace(strFkData, "${table}", table);
				}
				
				strFkData = StringUtil.replace(strFkData, "${field_code}", field);
				strFkData = StringUtil.replace(strFkData, "${field_alias}", field_alias);
				strFkData = StringUtil.replace(strFkData, "${field_reference}", fk_references);
				
				//替换系统内容参数
				TemplateEngine t3 = new TemplateEngine(null, null, strFkData);
				Recordset rsFkData = db.get(t3.toString());

				t.replace(rsFkData,"","rows");
			}
		}
        
        return t.toString();    
    }

    public String getCheckbox (Recordset rs,String te,String domain,String item) throws Throwable
    {
        //取域值
        TemplateEngine t1 = new TemplateEngine(null, null, domain);
        t1.replace(rs,"");      
        Recordset rsDomain = db.get(t1.toString());
        
        TemplateEngine t = new TemplateEngine(null, null, te);
        t.replace(rs,"");
        //不支持多个复选
        t.replace(rsDomain,"","rows");

        //设置默认值
        rsDomain.top();
        String strDefault = "";
        while(rsDomain.next())
        {
            strDefault = rsDomain.getString("is_default");
            if (strDefault!=null && strDefault.equals("1")){
                t.setRadioButton(rs.getString("form_field_code")+(item==null?"":"_"+item),rsDomain.getString("domain_value"));
                t.replace(rsDomain,"");
            }
        }

        return t.toString();    
    }

    public String getRadio (Recordset rs,String te,String domain,String strFkField,String strFkData,String item) throws Throwable
    {
        //取域值
        TemplateEngine t1 = new TemplateEngine(null, null, domain);
        t1.replace(rs,"");  
        Recordset rsDomain = db.get(t1.toString());
        
        TemplateEngine t = new TemplateEngine(null, null, te);
        t.replace(rs,"");

		if(rsDomain.getRecordCount()>0){
	        t.replace(rsDomain,"","rows");

	        //设置默认值
	        rsDomain.top();
	        String strDefault = "";
	        while(rsDomain.next())
	        {
	            strDefault = rsDomain.getString("is_default");
	            if (strDefault==null)
	                break;
	                
	            if (strDefault!=null && strDefault.equals("1")){
	                t.setRadioButton(rs.getString("form_field_code")+(item==null?"":"_"+item),rsDomain.getString("domain_value"));
	                t.replace(rsDomain,"");
	            }
	        }
		}else{
			TemplateEngine t2 = new TemplateEngine(null, null, strFkField);
			t2.replace(rs,"");		
			Recordset rsFkFiled = db.get(t2.toString());
			if(rsFkFiled.next()){
				rsFkFiled.first();
				String schema = rsFkFiled.getString("schema_code");
				String table = rsFkFiled.getString("table_code");
				String field = rsFkFiled.getString("field_code");
				String field_alias = rsFkFiled.getString("field_alias");
				String fk_references = rsFkFiled.getString("fk_references");
				String fk_sql = rsFkFiled.getString("fk_sql");

				//外键配置数据不完整
				if(table==null || field==null || field_alias==null)
				{
					return t.toString();
				}
				if(fk_sql!=null && !"".equals(fk_sql)){
					fk_sql = StringUtil.replace(fk_sql, "${DEF", "${def");
					String table_alias = "(" + fk_sql + ") " + table;
					strFkData = StringUtil.replace(strFkData, "${schema}", "");
					strFkData = StringUtil.replace(strFkData, "${table}", table_alias);
				}else{
					strFkData = StringUtil.replace(strFkData, "${schema}", schema+".");
					strFkData = StringUtil.replace(strFkData, "${table}", table);
				}
				
				strFkData = StringUtil.replace(strFkData, "${field_code}", field);
				strFkData = StringUtil.replace(strFkData, "${field_alias}", field_alias);
				strFkData = StringUtil.replace(strFkData, "${field_reference}", fk_references);

				//替换系统内容参数
				TemplateEngine t3 = new TemplateEngine(null, null, strFkData);
				Recordset rsFkData = db.get(t3.toString());

				t.replace(rsFkData,"","rows");
			}
		}

        return t.toString();        
    }

    public void setSpan (int[][] span,int row,int col,int rowspan,int colspan)
    {
        for (int i=row;i<row+rowspan;i++){
            int maxCols=col+colspan;
            if(maxCols>span[i].length)
                maxCols = span[i].length;

            for (int j=col;j<maxCols;j++){
                span[i][j]++;
            }
        }
    }

    public int getSpan (int[][] span,int row,int col)
    {
        return span[row][col];
    }

}
