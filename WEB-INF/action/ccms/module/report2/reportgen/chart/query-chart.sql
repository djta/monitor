SELECT
    c.chart_code  as  plugin    
    ,title
    ,title_x
    ,title_y
    ,width
    ,height
FROM
	t_report_chart rc
	left join t_chart c
	on rc.chart_id = c.tuid 
WHERE
    rc.tuid = ${fld:chart_id}
