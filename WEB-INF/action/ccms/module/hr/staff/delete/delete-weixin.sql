delete from 
	hr_staff_weixin
where 
    userlogin = (select userlogin from hr_staff where user_id = ${fld:id})
