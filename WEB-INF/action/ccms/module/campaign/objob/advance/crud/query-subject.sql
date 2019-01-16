SELECT
    t.tuid  as subject_id
    , t.subject_name
FROM
	t_subject t
	inner join cs_campaign c
	on t.tuid = c.subject_id
WHERE
    c.tuid = ${fld:campaign_id}
