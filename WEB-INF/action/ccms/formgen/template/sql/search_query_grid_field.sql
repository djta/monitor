SELECT
    case 
        when (select count(1) from t_domain d where lower(d.namespace)=lower(fi.domain_namespace))>0 
        then concat(concat(concat(concat(concat('(select max(d.domain_text_cn) as domain_text_cn from t_domain d where d.namespace=''',fi.domain_namespace),''' and d.domain_value = '),(case when case when fi.is_virtual_type is null then '0' else fi.is_virtual_type end ='0' then concat(t.table_code,'.') else '' end)),fi.field_code),' )') 
        when fi.fk_tab is not null
        then concat(concat(concat(concat(concat(concat(concat(concat(concat('(select ',fi.fk_fld_anchor),' from '),fi.fk_tab),' a where a.'),fi.fk_fld_id),'= '),(case when case when fi.is_virtual_type is null then '0' else fi.is_virtual_type end ='0' then concat(t.table_code,'.') else '' end)),fi.field_code),')')
      	when fi.show_type = '8' 
        then concat('cast(''', replace(fi.plugin_control, '''', ''''''), ''' as varchar)') 
        else concat((case when case when fi.is_virtual_type is null then '0' else fi.is_virtual_type end ='0' then concat(t.table_code,'.') else '' end) , lower(fi.field_code))  
        end  as	field_code_cn
    ,case 
        when (select count(1) from t_domain d where lower(d.namespace)=lower(fi.domain_namespace))>0 
        then concat(concat(concat(concat(concat('(select max(d.domain_text_en) as domain_text_en from t_domain d where d.namespace=''',fi.domain_namespace),''' and d.domain_value = '),(case when case when fi.is_virtual_type is null then '0' else fi.is_virtual_type end ='0' then concat(t.table_code,'.') else '' end)),fi.field_code),' )') 
        when fi.fk_tab is not null
        then concat(concat(concat(concat(concat(concat(concat(concat(concat('(select ',(case when fi.fk_references is not null then fi.fk_references else fi.fk_fld_anchor end)),' from '),fi.fk_tab),' a where a.'),fi.fk_fld_id),'= '),(case when case when fi.is_virtual_type is null then '0' else fi.is_virtual_type end ='0' then concat(t.table_code,'.') else '' end)),fi.field_code),')')
       	when fi.show_type = '8' 
        then concat('cast(''', replace(fi.plugin_control, '''', ''''''), ''' as varchar)') 
        else concat((case when case when fi.is_virtual_type is null then '0' else fi.is_virtual_type end ='0' then concat(t.table_code,'.') else '' end) , lower(fi.field_code))  
        end  as	field_code_en
    ,lower(fi.field_code_alias) as colname
    ,case when fi.field_type='varchar' then concat(concat(lower('${FLD:'),lower(fi.field_code_alias)),'@js}') else concat(concat(lower('${FLD:'),lower(fi.field_code_alias)),'}') end	as  colname_mark
	,case when ff.show_flag is null then '0' else ff.show_flag end as show_flag
	,case when ff.sort_order='0' then '' else ff.sort_order end as sort_order	/*0为手功排序,不拼到查询语句中*/
	,ff.compute_total
FROM
	t_form f
	inner join t_form_grid_field ff
	on ff.form_id = f.tuid
	inner join t_field fi
	on ff.field_id = fi.tuid
	inner join t_table t
	on fi.table_id = t.tuid
WHERE
	f.tuid = ${form_id}
order by
	ff.show_order