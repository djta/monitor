select
	case when is_ldap = 1 then 'LDAP' else 'DB' end as tipo,
	s.user_id,
	s.userlogin,
	s.passwd,
	s.lname,
	s.fname,
	s.email,
	s.pwd_policy,
	s.force_newpass,
	s.locale,
	s.is_ldap,
	s.enabled,
	s.dn,
	s.direccion_ip
from 
	${schema}s_user s
where 
	enabled = 1
	${filter}
	${orderby}