SELECT
    t.tuid  as form_id
    , t.form_name
FROM
	t_form t
where
	t.tuid = ${fld:form_id}