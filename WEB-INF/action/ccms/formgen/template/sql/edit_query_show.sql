SELECT
    lower(fi.field_code) as field_code
    ,lower(fi.field_code_alias) as colname
    ,concat(concat(lower('${FLD:'),case when fi.fk_tab is not null or fi.domain_namespace is not null then concat(lower(fi.field_code_alias),'_showonly') else lower(fi.field_code_alias) end), case when lower(fi.field_type)='varchar' then '@js}' when lower(fi.field_type)='date' then '@yyyy-MM-dd}' when lower(fi.field_type)='timestamp' then '@yyyy-MM-dd HH:mm:ss}' else '}' end) as field_mark
    ,case when case when fi.is_virtual_type is null then '0' else fi.is_virtual_type end='0' and case when ff.show_type is null then '0' else ff.show_type end!='11' then lower(fi.field_code) else lower(fi.field_code_alias) end as ctrl_code
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
case when ff.show_type is null then fi.show_type else ff.show_type end = '11'	/*仅显示*/
