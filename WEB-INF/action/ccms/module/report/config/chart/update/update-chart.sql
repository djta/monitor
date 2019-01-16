update
	t_report_chart
set
	remark =${fld:remark}
	,title  =${fld:title}
	,title_x =${fld:title_x}
	,title_y =${fld:title_y}
	,title_z =${fld:title_z}
	,field_x =${fld:field_x}
	,field_y =${fld:field_y}
	,field_z =${fld:field_z}
	,format_x =${fld:format_x}
	,format_y =${fld:format_y}
	,format_z =${fld:format_z}
	,chart_type =${fld:chart_type}
	,is_3d =${fld:is_3d}
	,callback_js =${fld:callback_js}
	,updatedby='${def:user}'
	,updated={ts '${def:timestamp}'}
where 
	tuid= ${fld:tuid}