UPDATE
	t_import_rule_filter
SET
	clause_code = ${fld:clause_code}
	,field_type = ${fld:field_type}
	,clause_filter = ${fld:clause_filter}
	,clause_value = ${fld:clause_value}
	,phrase_name = ${fld:phrase_name}
	,is_node = ${fld:is_node}
	,logic_type = ${fld:logic_type}
	,namespace = ${fld:namespace}
	,field_id = ${fld:field_id}
	,updatedby = '${def:user}'
	,updated = {ts '${def:timestamp}'}
WHERE
	tuid = ${fld:tuid}
