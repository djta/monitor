select
	s.user_id, 
	s.userlogin,
	concat(s.lname,s.fname) as username,
	t.name user_name,
	s.enabled,
	s.force_newpass,
	s.pwd_policy,
	s.locale,'${userlogin}' userlogin,
	s.is_ldap,staff_type user_type,case when cas_userlogin = '${casuser}' then '1' else '0' end as casmatched,
	case when t.tenantry_id is null then 0 else t.tenantry_id end as tenantry_id,
	case when t.org_id is null then 0 else t.org_id end as org_id,
	case when t.def_subject_id is null then 0 else t.def_subject_id end as subject_id
from 
	${schema}s_user s
	left join hr_staff t on s.user_id = t.user_id
where 
	s.userlogin = '${userlogin}'
or 
	s.cas_userlogin = '${casuser}'