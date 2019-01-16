SELECT
    tuid
    ,authority_name
    ,remark
FROM
	hr_authority
WHERE
	tuid=${fld:id}
