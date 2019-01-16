select
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
from ${schema}s_user s
where user_id = ${fld:id}
