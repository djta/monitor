update cc_server
set
	ip = ${fld:ip},
	type = ${fld:type},
	port = ${fld:port},
	username = ${fld:username},
	password = ${fld:password},
	updated = {ts '${def:timestamp}'},
	updatedby = '${def:user}'
where
	tuid = ${fld:tuid}
