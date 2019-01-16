SELECT
    tuid
	, authority_id
	, entity_name
	, entity_type
	, remark
FROM
	hr_authority_entity
WHERE
	tuid=${fld:id}
