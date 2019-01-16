UPDATE
	cs_job
SET
	is_enabled	 =${fld:enabled_status}
WHERE
	tuid	=${fld:id}
