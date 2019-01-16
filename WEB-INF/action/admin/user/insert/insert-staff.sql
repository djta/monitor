insert into hr_staff
(
	user_id,
	userlogin,
	name,
	email,
	sex,
	tenantry_id,
	def_subject_id
)
values 
(
	${seq:currval@${schema}seq_user},
	${fld:userlogin},
	concat(${fld:lname},${fld:fname}),
	${fld:email},
	'1',
	${fld:tenantry_id},
	(select subject_id from t_subject_tenantry where tenantry_id=${fld:tenantry_id} limit 1)
)
