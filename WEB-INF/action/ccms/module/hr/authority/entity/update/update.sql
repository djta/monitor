UPDATE
	hr_authority_entity
SET
	authority_id     =${fld:authority_id}
	,entity_name	 =${fld:entity_name}
	,entity_type        =${fld:entity_type}
	,remark			=${fld:remark}
	,updated              ={ts '${def:timestamp}'}
	,updatedby         ='${def:user}'
WHERE
	tuid	=${fld:tuid}
