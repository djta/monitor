select
	s.user_id, 
	s.userlogin,
	s.enabled,
	s.force_newpass,
	s.pwd_policy,
	s.locale,
	s.is_ldap,
	h.tenantry_id,
	h.def_subject_id as subject_id,
	h.org_id
from 
	${schema}s_user s
	inner join hr_staff h
	on s.userlogin = h.userlogin
where 
	exists (select 1 from hr_staff_weixin sw where sw.userlogin=h.userlogin and sw.weixin_userid = '${weixin_userid}')