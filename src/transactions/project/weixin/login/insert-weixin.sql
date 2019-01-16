INSERT	INTO
	hr_staff_weixin
(
	 tuid
	,userlogin
	,weixin_userid
	,enabled
	,weixin_service_id
	,created
)
VALUES
(
	 ${seq:nextval@${schema}seq_default}
	,'${userlogin}'
	,'${weixin_userid}'
	,'1'
	,${weixin_service_id}
	,{ts '${def:timestamp}'}
)