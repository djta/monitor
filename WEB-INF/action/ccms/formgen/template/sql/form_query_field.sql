SELECT
	case when case when fi.is_virtual_type is null then '0' else fi.is_virtual_type end='0' and case when ff.show_type is null then '0' else ff.show_type end !='11' then lower(fi.field_code) else lower(fi.field_code_alias) end	as field_code
	,case when case when fi.is_virtual_type is null then '0' else fi.is_virtual_type end='0' and case when ff.show_type is null then '0' else ff.show_type end !='11' then lower(fi.field_code) else lower(fi.field_code_alias) end as form_field_code
	,fi.field_name_cn  as  field_name
	,fi.field_name_cn  as  field_name_cn
	,fi.field_name_en  as  field_name_en
	,case when ff.show_type is null then fi.show_type else ff.show_type end as show_type
	,case when case when ff.show_type is null then fi.show_type else ff.show_type end ='0' then ff.width when case when ff.show_type is null then fi.show_type else ff.show_type end='1' and ff.width=0 then 180 else ff.width end as show_width
	,fi.field_length	as	maxlength
	,fi.tuid		as		field_id
	,f.col_num_edit	as	col_num
	,'formEditor'		as	pickup_form
	,fi.tuid		as	pickup_field
	,ff.is_mandatory		as	is_required	/*是否必填项*/
	,case when ff.is_mandatory='0' then 'required' else '' end as is_required_alias
	,case when ff.is_readonly='1' then case when case when ff.show_type is null then fi.show_type else ff.show_type end='1' or case when ff.show_type is null then fi.show_type else ff.show_type end='2' or case when ff.show_type is null then fi.show_type else ff.show_type end='3' then 'disabled' else 'class="readonly" readonly' end else '' end as is_readonly_alias
	,case when ff.is_attachment='1' then case when case when ff.show_type is null then fi.show_type else ff.show_type end='1' or case when ff.show_type is null then fi.show_type else ff.show_type end='2' or case when ff.show_type is null then fi.show_type else ff.show_type end='3' then 'none' else 'inline' end else 'none' end as is_attachment_alias
	,fi.format_mark			/*显示格式*/
	,case when ff.show_color is null then '' else concat('background:',ff.show_color) end as show_color
	,case when ff.colspan is null then 1 else ff.colspan end   as  colspan
	,case when ff.rowspan is null then 1 else ff.rowspan end   as  rowspan
	,case when lower(fi.default_value) is null then '' else lower(fi.default_value) end   as  default_value
	,replace(fi.plugin_code,'${FLD','${fld') as  plugin_code 
	,replace(fi.plugin_control,'${FLD','${fld') as  plugin_control
	,case when fi.domain_namespace is null then 'null' else fi.domain_namespace end  as  domain_namespace
	,case when case when ff.show_type is null then fi.show_type else ff.show_type end='9' then 99999 else ff.show_order end as field_show_order /*将hidden控件放最后*/
	,ff.item_id as form_item_id
	,ta.table_code /*用于字段挂附件*/
FROM
	t_form f
	left join t_form_show_field ff
	on ff.form_id = f.tuid
	left join t_field fi
	on ff.field_id = fi.tuid
	left join t_table ta
	on fi.table_id = ta.tuid
WHERE
	f.tuid = ${fld:form_id}
AND
	case when ff.item_id is null then 0 else ff.item_id end = ${fld:item_id}
order by 
	field_show_order