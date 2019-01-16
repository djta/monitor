SELECT
    tuid
    ,subject_name
    ,remark
FROM
	t_subject 
WHERE
	tuid=${fld:id}
