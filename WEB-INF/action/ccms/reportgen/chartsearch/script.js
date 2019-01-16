var $HighChartObj = $HighChart("response_${fld:report_id}",{"success":function(){$("#response_${fld:report_id}").show()},"config":function(){${fld:callback_js}}});
var data_array = [
	<rows>
	{
	'${fld:field_x}':'${fld:field_x_value}',
	'${fld:field_y}':${fld:field_y_value},
	'${fld:field_z}':'${fld:field_z_value}'
	},
	</rows>

{}];

data_array.pop();

$HighChartObj.getChartResult(
		{'title':'${fld:title}','titleX':'${fld:title_x}','titleY':'${fld:title_y}','titleZ':'${fld:title_z}','chart_type':'${fld:chart_type}','field_x':'${fld:field_x}','field_y':'${fld:field_y}','field_z':'${fld:field_z}','format_x':'${fld:format_x}','format_y':'${fld:format_y}','format_z':'${fld:format_z}','isShow3D':('1'=='${fld:is_3d}'?true:false)}
		,data_array
		);
