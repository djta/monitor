SELECT
	tuid
	,subject_name
	,remark
FROM
	t_subject
WHERE
	is_deleted = '0'
    
${filter}
