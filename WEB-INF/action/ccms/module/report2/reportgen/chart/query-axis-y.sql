SELECT
    lower(f.field_code_alias)  as  axis_y    
FROM
	t_report_chart c
	inner join t_report_show_field sf
	on sf.report_id=c.report_id
	and sf.is_axis_y='1'
	inner join t_field f
	on sf.field_id = f.tuid
WHERE
    c.tuid = ${fld:chart_id}
