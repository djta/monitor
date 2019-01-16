INSERT	INTO
	hr_staff_weixin
(
	 tuid
	,userlogin
	,weixin_userid
	,created
	,enabled
	,weixin_id
	,weixin_msgid
	,weixin_service_id
)
VALUES
(
	 ${seq:nextval@security.seq_default}
	,${fld:userlogin}
	,${fld:weixin_userid}
	,current_timestamp
	,'1'
	,${fld:weixin_id}
	,${fld:weixin_msgid}
	,(select tuid from wx_service t where t.wxid= ${fld:weixin_id})
)