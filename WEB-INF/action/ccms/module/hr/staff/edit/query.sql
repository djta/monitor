SELECT
	s.userlogin,
	s.name,
	s.sex,
	s.birthday,
	s.address,
	s.card_no,
	s.entry_date,
	s.contract_from,
	s.contract_end,
	s.salary,
	s.remark,
	s.email,
	s.userlogin as tuid,
	s.user_id,
	u.locale,
	s.mobile,
	s.staff_category,
	s.parent_user,
	(select name from hr_staff where userlogin = s.parent_user ) as names,
	s.staff_category,
	s.user_pinyin,
	s.remind_type,
	(select tuid from t_attachment_files where table_code='hr_staff' and pk_value=s.user_id::varchar order by tuid desc limit 1) as upload_id
	
FROM
	hr_staff s
	inner join ${schema}s_user u on s.userlogin=u.userlogin
WHERE
	s.userlogin = ${fld:id}