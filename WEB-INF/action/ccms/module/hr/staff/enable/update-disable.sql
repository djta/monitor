update ${schema}s_user set enabled = 1
where 
	user_id = ${fld:id}
and
	enabled = 0