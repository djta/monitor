update ${schema}s_user set enabled = 0
where 
	user_id = ${fld:id}
and
	enabled = 1