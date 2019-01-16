UPDATE
	cs_campaign
SET
	subject_id       =${fld:subject_id1}
	,campaign_name     =${fld:campaign_name}
	,campaign_name_en  =${fld:campaign_name_en}
	,campaign_code     =${fld:campaign_code}
	,campaign_type     =${fld:campaign_type1}
	,remark	 =${fld:remark}
	,launch_year	 =${fld:launch_year}
	,launch_city	 =${fld:launch_city}
	,partners_name	 =${fld:partners_name}
	,launch_platform	 =${fld:launch_platform}
	,priority	 =${fld:priority}
	, updated	={ts '${def:timestamp}'}
	, updatedby	='${def:user}'
	,is_recommend =${fld:is_recommend}
	,campaign_quota =${fld:campaign_quota}
WHERE
	tuid	=${fld:tuid}
