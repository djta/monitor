update cc_card d 
set 
	savestartdate = '${def:date}'::date,
	stopdays = d.stopdays + ('${def:date}'::date - s.startdate::date)::int,
	status = 1,
	enddate = d.enddate + ('${def:date}'::date - s.startdate::date)::int 
from cc_savestopcard s 
where s.code = ${fld:code} and d.code = s.cardcode 
and d.org_id = s.org_id and d.org_id = ${fld:org_id} and d.isgoon = 0 
