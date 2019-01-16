SELECT
	s.userlogin,
	s.name,
	s.sex,
	s.birthday,
	s.address,
	s.email,
	s.mobile,
FROM
	hr_staff s
	inner join ${schema}s_user u on s.userlogin=u.userlogin
WHERE
	s.userlogin = ${fld:id}