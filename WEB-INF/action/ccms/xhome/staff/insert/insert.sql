insert into hr_staff(
    user_id,
    userlogin,
    name,
    mobile,
    vc_contact,
    sex,
    tenantry_id,
    created,
    createdby
) values(
	${seq:nextval@${schema}seq_user},
    ${fld:userlogin},
    ${fld:name},
    ${fld:mobile},
    ${fld:vc_contact},
    ${fld:sex},
    ${def:tenantry},
	{ts '${def:timestamp}'},
	'${def:user}'
)
