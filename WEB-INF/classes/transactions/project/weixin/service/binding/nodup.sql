select 
	u.userlogin,u.passwd,0 as s_order 
from 
	${schema}s_user u
where 
	userlogin = ${fld:userlogin}
union
select 
	u.userlogin,u.passwd ,1 as s_order
from 
	${schema}s_user u
	join hr_staff_weixin sw on sw.userlogin=u.userlogin and sw.weixin_userid=${fld:weixin_userid}
order by 
	s_order