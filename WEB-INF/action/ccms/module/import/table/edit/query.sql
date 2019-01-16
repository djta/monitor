SELECT
	p.tuid
	,p.imp_id
	,p.table_id
	,p.tab_name
	,t.table_name
	,p.parent_id
	,m.tab_name as parent_name
	,p.bpk_field_alias
	,p.if_new_flag
	,p.remark
	,p.data_operator_type
FROM
	t_import_table p
	left join t_table t
	on p.table_id = t.tuid
	left join t_import_table m
	on p.parent_id = m.tuid
WHERE
	p.tuid=${fld:id}