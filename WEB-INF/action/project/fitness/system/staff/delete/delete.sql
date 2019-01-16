update    hr_staff
set status = 3
where
	user_id::varchar in (
		select regexp_split_to_table(${fld:id}, ',')
	)

