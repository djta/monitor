{"page":{"total":${fld:total},"pageNo":${fld:pageno},"totalPages":${fld:pages}},"rows":[
	<rows>
	{
		"userlogin":"${fld:userlogin@js}"
		,"login_date":"${fld:login_date@yyyy-MM-dd}"
		,"login_time":"${fld:login_time@js}"
		,"remote_addr":"${fld:remote_addr@js}"
		,"context":"${fld:context@js}"
		,"exit_date":"${fld:exit_date@yyyy-MM-dd  hh:mm:ss}"
	},
	</rows>
	{}
]}