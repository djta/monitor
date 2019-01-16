select 
	tuid,
	bannername,
	linkurl,
	attachid,
	showorder,
	created,
	createdby 
from hr_org_banner 
where tuid = ${fld:id}
