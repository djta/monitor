and
	(case when ${fld:daochu_statusviewtime}='1' then (m.viewtime is null) else 
		m.viewtime is not null
	end)
