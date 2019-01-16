INSERT	INTO
	hr_staff_weixin
(
	 tuid
	,userlogin
	,weixin_userid
	,created
	,enabled
	,weixin_service_id
	,longitude
	,latitude
)
VALUES
(
	 ${seq:nextval@${schema}seq_default}
	,${fld:userlogin}
	,${fld:weixin_id}
	,{ts '${def:timestamp}'}
	,'1'
	,${fld:service_id}
	,'${longitude}'
	,'${latitude}'
)