insert into hr_staff
(
	user_id,
	userlogin,
	name,
	email,
	mobile,
	sex,
	tenantry_id,
	def_subject_id
)
values 
(
	${seq:currval@${schema}seq_user},
	${fld:userlogin},
	${fld:name},
	${fld:email},
	${fld:mobile},
	${fld:sex},
	${fld:tenantry_id},
	(select subject_id from t_subject_tenantry where tenantry_id=${fld:tenantry_id} limit 1)
)
