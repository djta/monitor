select 
	c.tuid 
	,c.remark
	,c.title
	,c.title_x
	,c.title_y
	,c.title_z
	,c.field_x
	,c.field_y
	,c.field_z
	,c.format_x
	,c.format_y
	,c.format_z
	,c.chart_type
	,c.is_3d
	,c.callback_js
	,c.report_id
from 
	t_report_chart c 
where 
	c.report_id = ${fld:report_id}
union 
select 
	null as tuid 
	,null as remark
	,null as title
	,null as title_x
	,null as title_y
	,null as title_z
	,null as field_x
	,null as field_y
	,null as field_z
	,null as format_x
	,null as format_y
	,null as format_z
	,null as chart_type
	,null as is_3d
	,null as callback_js
	,null as report_id 
from dual