select
	user_id as id,
	userlogin
from ${schema}s_user
where user_id = ${fld:id}