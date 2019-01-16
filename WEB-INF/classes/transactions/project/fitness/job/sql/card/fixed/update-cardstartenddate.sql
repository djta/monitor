UPDATE cc_card set
	status =1,
	startdate={d '${def:date}'},
	enddate=({d '${def:date}'}+(totalday||' d')::interval+(giveday||' d')::interval)::date
WHERE
	code = ${fld:code} and org_id = ${fld:org_id}
	and status=2 and starttype=1
	and isgoon = 0