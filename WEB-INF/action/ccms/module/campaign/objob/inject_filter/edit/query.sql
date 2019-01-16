SELECT
    t.tuid
    ,t.job_id
    ,t.form_id
    ,t.clause_code
    ,d.field_name_${def:locale}  as  field_name
    ,t.clause_filter
    ,t.clause_value
    ,t.is_node
    ,t.parent_id
    ,t.logic_type
    ,t.namespace
    ,t.field_type
    ,t.phrase_name
    ,t.filter_type
FROM
	cs_job_filter t
	left join t_form f on t.form_id = f.tuid
	left join t_field d on (f.table_id=d.table_id and t.clause_code=d.field_code)
WHERE
	t.tuid=${fld:id}
