SELECT
    c.tuid  as  chart_id    
FROM
	t_report_chart c
WHERE
    c.report_id = ${fld:report_id}
