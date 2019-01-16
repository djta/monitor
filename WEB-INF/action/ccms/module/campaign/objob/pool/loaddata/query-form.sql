SELECT
    t.tuid  as form_id
    , t.table_id
    , t.subject_id
    , t.form_name
    , s.subject_name
    , ta.table_name
    , ta.table_code
    , ta.schema_name
    , t.access_type
FROM
	t_form t
	left join t_table ta on t.table_id = ta.tuid
	left join t_subject s on t.subject_id = s.tuid
WHERE
    t.tuid = ${fld:form_id}
