SELECT
    t.tuid
    , t.subject_id
    , t.campaign_name
    , t.campaign_name_en
    , t.campaign_quota
    , t.campaign_code
    , t.campaign_type
    , case when t.campaign_status = '0' then '初始'
    		when t.campaign_status = '1' then '正常'
    		when t.campaign_status = '2' then '停止'
      end as campaign_status
    , t.remark
    , case 
    		when t.campaign_status is null then concat('<a href="javascript:void(0);" onclick="javascript:setCampaignStatus(''' , cast(tuid as char),''',1);" title="启用">启用</a>')
    		when t.campaign_status = '0' then concat('<a href="javascript:void(0);" onclick="javascript:setCampaignStatus(''' , cast(tuid as char), ''',1);" title="启用">启用</a>')
    		when t.campaign_status = '1' then concat('<a href="javascript:void(0);" onclick="javascript:setCampaignStatus(''' , cast(tuid as char), ''',2);" title="停止">停止</a>')
    		when t.campaign_status = '2' then concat('<a href="javascript:void(0);" onclick="javascript:setCampaignStatus(''' , cast(tuid as char), ''',1);" title="启用">启用</a>')
      end as url_status
FROM
	cs_campaign t
WHERE   
   ( t.is_deleted is null or t.is_deleted='0')
   
${filter}
${orderby}
	