SELECT
    t.tuid
    ,t.table_alias
    ,t.schema_name
    ,t.table_name
    ,t.table_code
    ,t.table_type
    ,t.bpk_field
    ,t.bpk_field_prefix
    ,t.bpk_field_seq
    ,t.delete_field
    ,t.subject_id
    ,t.remark
    ,t.bpk_field_type
    ,f.name_alias as type_alias
FROM
	t_table t
	left join t_field_type f on t.bpk_field_type = f.name
	
WHERE
	t.tuid=${fld:id}
