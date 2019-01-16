delete from     hr_team_staff
where
	team_id::varchar in (
		select regexp_split_to_table(${fld:id}, ';')
	)