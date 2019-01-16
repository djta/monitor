SELECT
    t.tuid
    ,t.subject_id
    ,t.campaign_name
    ,t.campaign_name_en
--    ,t.campaign_quota
    ,t.campaign_code
    ,t.campaign_type
    ,t.remark
    ,t.launch_year
    ,t.launch_city
    ,t.partners_name
    ,t.launch_platform
    ,s.subject_name
    ,t.priority
    ,t.is_recommend
FROM
	cs_campaign t
	left join t_subject s
	on t.subject_id = s.tuid
WHERE
	t.tuid=${fld:id} 
