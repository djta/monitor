insert into cs_job_filter(
	tuid
	,job_id
	,clause_code
	,field_type
	,clause_filter
	,clause_value
	,phrase_name
	,is_node
	,parent_id
	,logic_type
	,namespace
	,createdby
	,created
	,updatedby
	,updated
	,filter_type
)
VALUES
(
	${seq:nextval@seq_default}
	,${fld:job_id}
	,${fld:clause_code_id}
	,${fld:field_type}
	,${fld:clause_filter}
	,${fld:clause_value}
	,${fld:phrase_name}
	,${fld:is_node}
	,${fld:parent_id}
	,${fld:logic_type}
	,${fld:namespace}
	,'${def:user}'
	,{ts '${def:timestamp}'}
	,'${def:user}'
	,{ts '${def:timestamp}'}
	,${fld:filter_type}
)
