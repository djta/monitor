update 
	hr_staff
set	
	weixin_lastlogin = '${weixin_userid}',
	weixin_service_id = ${weixin_service_id}
where
	userlogin = '${userlogin}'