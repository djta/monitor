and
	user_id IN (select user_id from ${schema}s_user_role where role_id = ${fld:role_id})
