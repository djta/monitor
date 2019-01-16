SELECT
    t.tuid  as campaign_id
    , t.campaign_name
FROM
	cs_campaign t
WHERE
    t.tuid = ${fld:campaign_id}
