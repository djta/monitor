update 
	hr_staff
set	
	weixin_lastlogin = ${fld:weixin_id},
	weixin_service_id = ${fld:service_id}
where
	userlogin = ${fld:userlogin}