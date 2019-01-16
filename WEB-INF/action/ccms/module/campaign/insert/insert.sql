INSERT	INTO
cs_campaign
(
	tuid
	, subject_id
	, campaign_name
	, campaign_name_en
	, campaign_quota
	, campaign_code
	, campaign_type
	, campaign_status
	, remark
	, launch_year
	, launch_city
	, partners_name
	, launch_platform
	, priority
	, created
	, createdby
	, updated
	, updatedby
	, is_recommend
	, is_deleted
)
VALUES
(
	${seq:nextval@seq_cs_campaign}
	,${fld:subject_id1}
	,${fld:campaign_name}
	,${fld:campaign_name_en}
	,${fld:campaign_quota}
	,${fld:campaign_code}
	,${fld:campaign_type1}
	,'0'	/*0-inital,1-running,2-stoped*/
	,${fld:remark}
	,${fld:launch_year}
	,${fld:launch_city}
	,${fld:partners_name}
	,${fld:launch_platform}
	,${fld:priority}
	, {ts '${def:timestamp}'}
	, '${def:user}'
	, {ts '${def:timestamp}'}
	, '${def:user}'
	,${fld:is_recommend}
	,'0'
)