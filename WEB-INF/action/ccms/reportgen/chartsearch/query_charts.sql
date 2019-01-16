SELECT
c.tuid,
c.title,
c.title_x,
c.title_y,
c.title_z,
(select f.field_code from t_field f where f.tuid=c.field_x) as field_x,
(select f.field_code from t_field f where f.tuid=c.field_y) as field_y,
(select f.field_code from t_field f where f.tuid=c.field_z) as field_z,
c.format_x,
c.format_y,
c.format_z,
c.chart_type,
c.is_3d,
c.callback_js,
'${fld:'||(select f.field_code from t_field f where f.tuid=c.field_x)||'}' as field_x_value,
'${fld:'||(select f.field_code from t_field f where f.tuid=c.field_y)||'}' as field_y_value,
'${fld:'||(select f.field_code from t_field f where f.tuid=c.field_z)||'}' as field_z_value
FROM
	t_report_chart c
	join t_report r on c.report_id=r.tuid
WHERE
    c.report_id = ${fld:report_id}
and r.engine_type='2'
and r.enabled='1'