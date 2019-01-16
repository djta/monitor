select 
	t1.userlogin,t1.name,t1.mobile,t1.email,(select weixin_userid from hr_staff_weixin sw where sw.userlogin=t1.userlogin limit 1) as weixin_userid,t2.enabled,t1.is_weixin_sys 
from 
	hr_staff  t1   
	join security.s_user t2  on t1.user_id=t2.user_id   
where 
	t1.org_id='${orgId}'
and 
	(t1.email is not null or t1.mobile is not null)
and
	t2.enabled='1'