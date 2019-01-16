SELECT
	jor_file_name as report_path
	,congnos_ip
FROM
	t_report f
WHERE
	f.tuid = ${fld:report_id}
