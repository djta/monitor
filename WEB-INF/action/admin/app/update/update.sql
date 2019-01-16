update ${schema}s_application set 

	app_alias = ${fld:app_alias},
	description = ${fld:description},
	pwd_policy = ${fld:pwd_policy},
	updated = {ts '${def:timestamp}'},
	updatedby = '${def:user}'
where
	app_id = ${fld:tuid}
