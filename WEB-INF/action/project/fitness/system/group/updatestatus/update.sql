update    hr_team 
set  status = ${fld:status} 
where
	team_id::varchar in (
		select regexp_split_to_table(${fld:id}, ';')
	)


