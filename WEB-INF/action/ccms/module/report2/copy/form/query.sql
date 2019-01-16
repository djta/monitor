SELECT
    t.report_name_cn as old_name
    ,t.tuid as report_id
FROM
	t_report t
where
	t.tuid = ${fld:report_id}