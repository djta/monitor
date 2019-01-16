INSERT INTO t_import_batch
(
	tuid
	,table_id
	,table_code
	,op_type
	,pk_value
	,subject_id
	,imp_id
	,created
	,createdby
	,import_batch
)
VALUES
(
	${seq:nextval@${schema}seq_default}
	,?
	,?
	,?
	,?
	,?
	,${fld:imp_id}
	,{ts '${def:timestamp}'}
	,'${def:user}'
	,${fld:import_batch}
)
