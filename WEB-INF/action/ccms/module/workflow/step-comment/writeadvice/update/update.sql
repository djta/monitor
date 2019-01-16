UPDATE
	os_entry_comment
SET
	entry_id	 =${fld:entry_id}
	,node_id        =${fld:node_id}
	,comments	=${fld:owenadvice}
	,updated       ={ts '${def:timestamp}'}
	,updatedby     ='${def:user}'
	,snapshot = snapshot + 1
WHERE
	tuid	=${fld:tuid}
