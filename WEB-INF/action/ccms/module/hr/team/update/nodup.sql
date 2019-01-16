select 
	team_name 
from 
	hr_team
where 
	team_name = ${fld:team_name}
and
	tenantry_id = ${def:tenantry}
and
	team_id <> ${fld:tuid}