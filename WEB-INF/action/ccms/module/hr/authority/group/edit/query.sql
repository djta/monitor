SELECT
    tuid
    ,group_name
    ,remark
FROM
	hr_authority_group
WHERE
	tuid=${fld:id}
