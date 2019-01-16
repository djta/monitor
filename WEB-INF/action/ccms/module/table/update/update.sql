UPDATE
	t_table
SET
	schema_name     =${fld:schema_name}
	,table_name     =${fld:table_name}
	,table_alias     =${fld:table_alias}
	,table_code    =${fld:table_code}
	,table_type    =${fld:table_type}
	,bpk_field    =${fld:bpk_field}
	,bpk_field_prefix    =${fld:bpk_field_prefix}
	,bpk_field_seq    =${fld:bpk_field_seq}
	,delete_field	 =${fld:delete_field}
	,bpk_field_type = ${fld:bpk_field_type} 
	,remark	 =${fld:remark}
WHERE
	tuid	=${fld:tuid}
