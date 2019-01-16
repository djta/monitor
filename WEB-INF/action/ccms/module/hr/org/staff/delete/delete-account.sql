delete from 
	ws_account
where 
    userlogin = (select userlogin from hr_staff where user_id = ${fld:id})
