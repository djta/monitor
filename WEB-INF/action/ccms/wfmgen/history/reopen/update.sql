UPDATE
	os_wfentry
SET
	state = 1
WHERE
	id	= (select entry_id from os_historystep where id = ${fld:history_id})
