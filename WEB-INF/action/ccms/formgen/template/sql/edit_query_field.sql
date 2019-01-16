SELECT
    case 
 		when fi.show_type = '8' 
        then concat('cast(''', replace(fi.plugin_control, '''', ''''''), ''' as varchar)') 
      when (case when ff.show_type is null then '1' else ff.show_type end) != '4' then lower(fi.field_code)
        when (select count(*) from t_domain d where d.namespace=fi.domain_namespace)>0 
        then concat(concat(concat(concat(concat('(select d.domain_text_${def:locale} as domain_text from t_domain d where d.namespace=''',fi.domain_namespace),''' and d.domain_value = '),(case when case when fi.is_virtual_type is null then '0' else fi.is_virtual_type end='0' then concat(t.table_code,'.') else '' end)),fi.field_code),')')
        
        /*when fi.fk_tab is not null
        then concat('(select ',fi.fk_fld_anchor,' from ',fi.fk_tab,' a where a.',fi.fk_fld_id,'= ',(case when case when fi.is_virtual_type is null then '0' else fi.is_virtual_type end='0' then concat(t.table_code,'.') else '' end),fi.field_code,')')*/
        else lower(fi.field_code)  
        end  as	field_code
	,case when case when fi.is_virtual_type is null then '0' else fi.is_virtual_type end='0' and case when ff.show_type is null then '0' else ff.show_type end !='11' then lower(fi.field_code) else lower(fi.field_code_alias) end	as ctrl_code		/*form field control code*/
    ,lower(fi.field_code_alias) as colname
    ,concat(concat(lower('${FLD:'),lower(fi.field_code_alias)),case when lower(fi.field_type)='varchar' then '@js}' when lower(fi.field_type)='date' then '@yyyy-MM-dd}' when lower(fi.field_type)='timestamp' then '@yyyy-MM-dd HH:mm:ss}' else '}' end) as  field_mark
    ,fi.field_name_cn  as  field_name_cn
    ,fi.field_name_en  as  field_name_en
    ,ff.item_id as form_item_id
    ,ff.show_type
FROM
	t_form f
	inner join t_form_show_field ff on ff.form_id = f.tuid
	inner join t_field fi on ff.field_id = fi.tuid
	inner join t_table t on fi.table_id = t.tuid
WHERE
    f.tuid = ${form_id}

union

SELECT
    concat(concat(concat(concat(concat(concat(concat(concat(concat('(select ',fi.fk_fld_anchor),' from '),fi.fk_tab),' a where a.'),fi.fk_fld_id),' = '),(case when case when fi.is_virtual_type is null then '0'  else fi.is_virtual_type end='0' then concat(t.table_code,'.') else '' end)),fi.field_code),')')		as	field_code
	,concat(case when case when fi.is_virtual_type is null then '0' else fi.is_virtual_type end='0' and case when ff.show_type is null then '0' else ff.show_type end!='11' then lower(fi.field_code) else lower(fi.field_code_alias) end ,'_alias')	as ctrl_code
    ,concat(lower(fi.field_code_alias),'_alias') as colname
    ,concat(concat(concat(lower('${FLD:'),lower(fi.field_code_alias)),'_alias'),case when lower(fi.field_type)='varchar' then '@js}' when lower(fi.field_type)='date' then '@yyyy-MM-dd}' when lower(fi.field_type)='timestamp' then '@yyyy-MM-dd HH:mm:ss}' else '}' end)	as  field_mark
    ,fi.field_name_cn  as  field_name_cn
    ,fi.field_name_en  as  field_name_en
    ,ff.item_id as form_item_id
    ,ff.show_type
FROM
	t_form f
	inner join t_form_show_field ff on ff.form_id = f.tuid
	inner join t_field fi on ff.field_id = fi.tuid and fi.fk_tab is not null
	inner join t_table t on fi.table_id = t.tuid
WHERE
    f.tuid = ${form_id}

union

/*如果外键字段或域值字段有仅显示,则拼出结尾为_showonly的字段*/
SELECT
	case when fi.fk_fld_id is not null 
		then concat(concat(concat(concat(concat(concat(concat(concat(concat('(select ',fi.fk_fld_anchor),' from '),fi.fk_tab),' a where a.'),fi.fk_fld_id),' = '),(case when case when fi.is_virtual_type is null then '0' else fi.is_virtual_type end='0' then concat(t.table_code,'.') else '' end)),fi.field_code),')')		
    when (select count(*) from t_domain d where d.namespace=fi.domain_namespace)>0 
        then concat(concat(concat(concat(concat('(select d.domain_text_${def:locale} as domain_text from t_domain d where d.namespace=''',fi.domain_namespace),''' and d.domain_value = '),(case when case when fi.is_virtual_type is null then '0' else fi.is_virtual_type end='0' then concat(t.table_code,'.') else '' end)),fi.field_code),')')
    else '' end
     as	field_code
	,concat(case when case when fi.is_virtual_type is null then '0' else fi.is_virtual_type end='0' and case when ff.show_type is null then '0' else ff.show_type end!='11' then lower(fi.field_code) else lower(fi.field_code_alias) end ,'_alias')	as ctrl_code
    ,concat(lower(fi.field_code_alias),'_showonly') as colname
    ,concat(concat(concat(lower('${FLD:'),lower(fi.field_code_alias)),'_showonly'),case when lower(fi.field_type)='varchar' then '@js}' when lower(fi.field_type)='date' then '@yyyy-MM-dd}' when lower(fi.field_type)='timestamp' then '@yyyy-MM-dd HH:mm:ss}' else '}' end)	as  field_mark
    ,fi.field_name_cn  as  field_name_cn
    ,fi.field_name_en  as  field_name_en
    ,ff.item_id as form_item_id
    ,ff.show_type
FROM
	t_form f
	inner join t_form_show_field ff on ff.form_id = f.tuid and ff.show_type='11'
	inner join t_field fi on ff.field_id = fi.tuid and (fi.fk_tab is not null or fi.domain_namespace is not null)
	inner join t_table t on fi.table_id = t.tuid
WHERE
    f.tuid = ${form_id}

