update cc_device
set
	name=${fld:name},
	device_num=${fld:device_num},
	ip = ${fld:ip},
	c_type = ${fld:c_type},
	port = ${fld:port},
	username = ${fld:username},
	password = ${fld:password},
	updated = {ts '${def:timestamp}'},
	updatedby = '${def:user}'
where
	tuid = ${fld:tuid}
