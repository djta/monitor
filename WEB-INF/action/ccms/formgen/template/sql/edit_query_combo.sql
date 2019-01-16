SELECT
    lower(fi.field_code) as field_code
    ,lower(fi.field_code_alias) as colname
    ,concat(concat(lower('${FLD:'),lower(fi.field_code_alias)),'}') as field_mark
    ,case when case when fi.is_virtual_type is null then '0' else fi.is_virtual_type end ='0' then lower(fi.field_code) else lower(fi.field_code_alias) end as ctrl_code
    ,fi.field_name_cn  as  field_name_cn
    ,fi.field_name_en  as  field_name_en
    ,ff.item_id as form_item_id
FROM
	t_form f
	inner join t_form_show_field ff
	on ff.form_id = f.tuid
	inner join t_field fi
	on ff.field_id = fi.tuid
WHERE
    f.tuid = ${form_id}
AND
	case when ff.show_type is null then fi.show_type else ff.show_type end  = '1'	/*combobox*/
