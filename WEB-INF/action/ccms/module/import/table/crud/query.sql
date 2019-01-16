SELECT
	subject_id
	,tuid as imp_id
FROM
	t_import
WHERE
	tuid=${fld:imp_id}
	
