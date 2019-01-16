delete from 
	ws_account_list
where 
    userlogin = (select userlogin from hr_staff where user_id = ${fld:id})
