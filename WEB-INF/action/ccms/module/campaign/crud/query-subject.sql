SELECT
    t.tuid  as subject_id
    ,t.tuid as subject_id1
    ,t.subject_name as subject_name
    ,t.subject_name as subject_name1
FROM
	t_subject t
WHERE
    t.tuid = (case when ${fld:subject_id} is not null then ${fld:subject_id} else ${def:subject} end)
