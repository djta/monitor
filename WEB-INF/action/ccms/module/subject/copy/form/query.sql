SELECT
    t.subject_name as old_name
    ,t.tuid as subject_id
FROM
	t_subject t
where
	t.tuid = ${fld:subject_id}