SELECT
	lower(fi.field_code)	as	field_code
	,concat(lower(fi.field_code) , '_filter') as form_field_code
	,concat(concat(lower('${FLD:'),lower(fi.field_code_alias)),'}')	as  colname_mark
	,fi.field_code_alias
	,fi.field_name_cn as field_name
	,fi.field_name_cn  as  field_name_cn
	,fi.field_name_en  as  field_name_en
	,case when ff.show_type is null then fi.show_type else ff.show_type end as show_type
	,case when case when ff.show_type is null then fi.show_type else ff.show_type end='0' then ff.width when case when ff.show_type is null then fi.show_type else ff.show_type end='1' and ff.width=0 then 180 else ff.width end as show_width									/*显示宽度*/
	,fi.field_length	as	maxlength		/*最大长度*/
	,fi.tuid		as		field_id  
	,'formFilter'		as	pickup_form		/*pickup控件参数*/
	,fi.tuid		as	pickup_field		/*pickup控件参数*/
	,ff.is_mandatory		as	is_required	/*是否必填项*/
	,case when ff.is_mandatory='0' then 'required' else '' end as is_required_alias
	,fi.format_mark			/*显示格式*/
	,replace(fi.plugin_code,'${FLD','${fld') as  plugin_code 
	,replace(fi.plugin_control,'${FLD','${fld') as  plugin_control
	,case when lower(fi.default_value) is null then '' else lower(fi.default_value) end   as  default_value
	,case when fi.domain_namespace is null then 'null' else fi.domain_namespace end  as  domain_namespace
	,case when ff.filter_type is null then '=' else ff.filter_type end  as  filter_type
	,case when case when ff.show_type is null then fi.show_type else ff.show_type end='9' then 99999 else ff.show_order end as field_show_order /*将hidden控件放最后*/
	,case when ff.colspan is null then 1 else ff.colspan end   as  colspan
	,case when ff.rowspan is null then 1 else ff.rowspan end   as  rowspan
FROM
	t_form f
	inner join t_form_filter_field ff
	on ff.form_id = f.tuid
	inner join t_field fi
	on ff.field_id = fi.tuid
WHERE
	f.tuid = ${fld:form_id}
AND
case when ff.item_id is null then 0 else ff.item_id end = ${fld:item_id}
order by 
	field_show_order