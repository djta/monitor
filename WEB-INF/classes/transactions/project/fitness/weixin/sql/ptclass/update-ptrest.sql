update
	cc_ptrest
set
	ptleftcount = (ptleftcount-1)
where code = ${fld:ptrestcode} and org_id = ${fld:org_id}
