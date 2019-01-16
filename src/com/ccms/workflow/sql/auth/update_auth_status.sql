update 
	hr_grant
set
	status = (
		case when terminate_time is not null then '2'
			 when end_time < {ts '${def:timestamp}'} then '1'
			 else status end
	)
where	
	status = '0'