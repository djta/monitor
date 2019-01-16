update    ${schema}s_user
set enabled = 0 
where
	user_id::varchar in (
		select regexp_split_to_table(${fld:id}, ',')
	)

