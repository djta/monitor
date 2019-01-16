UPDATE
	os_entry_common_used
SET
	comments	 =${fld:usedcommon}
	,updated       ={ts '${def:timestamp}'}
	,updatedby     ='${def:user}'
WHERE
	tuid	=${fld:common_id}
