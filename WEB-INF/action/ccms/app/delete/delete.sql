UPDATE cc_app_version 
SET 
	is_deleted = 1,
	updated = {ts '${def:timestamp}'} 
WHERE tuid = ${fld:tuid} AND is_deleted = 0 
