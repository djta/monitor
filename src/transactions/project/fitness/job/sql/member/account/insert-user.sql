INSERT INTO security.s_user(
	user_id
	,userlogin
	,passwd
	,fname
	,enabled
	,pwd_policy
	,locale
) VALUES(
	currval('security.seq_user')
	,${fld:userlogin}
	,'${passwd}'
	,${fld:name}
	,1
	,-2
	,'cn'
)