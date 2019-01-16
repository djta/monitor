SELECT
	case is_virtual_type when '0' then lower(fi.field_code) else lower(fi.field_code_alias) end	as	field_code
	,t.bpk_field
	,case when fi.insert_phrase is not null
		then lower(fi.insert_phrase)
		else concat(concat(lower('${FLD:'),lower(fi.field_code)),'}')	
		end as  colname_mark
	,fi.field_name_cn  as  field_name_cn
	,fi.field_name_en  as  field_name_en
	,ff.is_mandatory		as	is_required	/*是否必填项*/
	,case when ff.is_mandatory='0' then 'required' else '' end as is_required_alias
	,fi.format_mark			/*显示格式*/
	,lower(fi.field_type)	as	field_type
	,fi.field_length
	,fi.is_virtual_type			/*非虚拟字段*/
FROM
	t_form f
	inner join t_form_show_field ff on ff.form_id = f.tuid and case when ff.show_type is null then '0' else ff.show_type end !='11'	/*排除仅显示字段*/
	inner join t_field fi on ff.field_id = fi.tuid	and fi.field_code is not null
	left join t_table t on f.table_id = t.tuid
WHERE
	f.tuid = ${form_id}
and 
	lower(fi.field_code)!=lower(t.bpk_field)
