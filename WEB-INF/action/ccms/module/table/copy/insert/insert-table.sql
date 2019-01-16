INSERT	INTO
t_table
(
	tuid, subject_id, table_alias, 
       table_name, table_code, state, enabled, remark, 
       created, createdby, updated, updatedby,
       deleted, table_created, schema_name, bpk_field, table_type, bpk_field_prefix, 
       bpk_field_seq, delete_field ,bpk_field_type
)
select
	${table_id}
	,${fld:subject_id}
	,${fld:table_alias}
	,concat(table_name,' - Copy'), table_code, state, enabled, remark, 
       created, createdby, updated, updatedby,
       deleted, table_created, schema_name, bpk_field, table_type, bpk_field_prefix, 
       bpk_field_seq, delete_field ,bpk_field_type
from
	t_table
where
	tuid = ${fld:table_id}