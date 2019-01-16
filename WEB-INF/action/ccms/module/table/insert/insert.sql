INSERT	INTO
t_table
(
	tuid
	, subject_id
	, schema_name
	, table_name
	, table_alias
	, table_code
	, table_type
	, bpk_field
	, bpk_field_type
	, bpk_field_prefix
	, bpk_field_seq
	, delete_field
	, remark
	,state
	,enabled
	,deleted
)
VALUES
(
	${seq:nextval@seq_table}
	,${fld:subject_id}
	,${fld:schema_name}
	,${fld:table_name}
	,${fld:table_alias}
	,${fld:table_code}
	,${fld:table_type}
	,${fld:bpk_field}
	,${fld:bpk_field_type}
	,${fld:bpk_field_prefix}
	,${fld:bpk_field_seq}
	,${fld:delete_field}
	,${fld:remark}
	,0
	,1
	,0
)