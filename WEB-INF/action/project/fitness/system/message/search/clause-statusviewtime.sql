and
	(case when ${fld:statusviewtime}='1' then (m.viewtime is null) else 
		m.viewtime is not null
	end)
