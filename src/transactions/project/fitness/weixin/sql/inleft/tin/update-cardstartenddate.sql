UPDATE cc_card 
SET 
	status = 1,
	startdate = {d '${def:date}'},
	enddate = ({d '${def:date}'}+(totalday||' d')::interval+(giveday||' d')::interval)::date
WHERE code = ${fld:cardcode} AND org_id = ${fld:org_id} 
AND status = 2 AND starttype = 1