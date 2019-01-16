SELECT
	lower(fi.field_code_alias) as field_code_alias
	,case when ff.show_type is null then fi.show_type else ff.show_type end as show_type
	,lower(fi.field_code)	as	field_code
	,concat(concat(lower('${FLD:'),lower(fi.field_code_alias)),'}')	as  colname_mark
	,fi.field_name_${def:locale}  as  field_name
	,case when case when ff.show_type is null then fi.show_type else ff.show_type end ='0' then ff.width when case when ff.show_type is null then fi.show_type else ff.show_type end='1' and ff.width=0 then 180 else ff.width end as show_width									/*显示宽度*/
	,fi.field_length	as	maxlength		/*最大长度*/
	,fi.tuid		as		field_id  
	,f.col_num_filter	as	col_num			/*查询界面显示列数*/
	,'formFilter'		as	pickup_form		/*pickup控件参数*/
	,fi.tuid		as	pickup_field		/*pickup控件参数*/
	,ff.is_mandatory		as	is_required	/*是否必填项*/
	,case when ff.is_mandatory='0' then ' required' else '' end as is_required_alias
	,fi.format_mark			/*显示格式*/
	,case when lower(fi.default_value) is null then '' else lower(fi.default_value) end   as  default_value
	,case when fi.domain_namespace is null then 'null' else fi.domain_namespace end  as  domain_namespace
	,case when case when ff.show_type is null then fi.show_type else ff.show_type end ='8' then  replace(replace(case when fi.plugin_control is null then '' else fi.plugin_control end ,'${LBL','${lbl'),'${DEF','${def') else '' end as  plugin_control
	,case when ff.filter_type is null then '=' else ff.filter_type end as filter_type
	,ff.filter_value
	,replace(case when fi.plugin_code is null then '' else fi.plugin_code end,'${LBL','${lbl') as  plugin_code
	,case when f.col_num_filter=0 then 12 else 12/f.col_num_filter end as colspan
FROM
	t_report f
	inner join t_report_filter_field ff
	on ff.report_id = f.tuid
	inner join t_field fi
	on ff.field_id = fi.tuid
WHERE
	f.tuid = ${fld:report_id}
order by 
	ff.show_order