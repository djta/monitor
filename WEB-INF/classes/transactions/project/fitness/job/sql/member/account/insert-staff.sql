INSERT INTO hr_staff(
	user_id
	,userlogin
	,name
	,mobile
	,created
	,createdby
	,org_id
	,status
	,is_member
) VALUES(
	nextval('security.seq_user')
	,${fld:userlogin}
	,(CASE WHEN ${fld:name} IS NULL OR ${fld:name} = '' THEN '会员' ELSE ${fld:name} END)
	,${fld:mobile}
	,{ts '${def:timestamp}'}
	,'sys'
	,${fld:org_id}
	,1
	,1
)