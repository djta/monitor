update cc_inleft 
set
	lefttime = {ts'${def:timestamp}'},
	leftuser = 'sys' 
where code = ${fld:code} and org_id = ${fld:org_id}
