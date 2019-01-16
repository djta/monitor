select
	user_id,
	lname,
	fname
from
	 ${schema}s_user
where
	user_id = ${fld:user_id}