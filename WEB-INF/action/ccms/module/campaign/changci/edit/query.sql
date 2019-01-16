select
  tuid
  ,campaign_id
  ,(select  campaign_name from cs_campaign where tuid=p.campaign_id) as campaign_name    
  ,changci_name
  ,changci_time
 --  ,DATE_FORMAT(regist_from_time,'%Y-%m-%d %H:%i:%s') as regist_from_time
--   ,DATE_FORMAT(regist_to_time,'%Y-%m-%d %H:%i:%s') as regist_to_time
  ,regist_from_time
  ,regist_to_time
  ,(SELECT parent_domain_value FROM t_domain WHERE namespace = 'City' AND domain_value = event_city) as province    
  ,event_city
  ,car_series
  ,show_order
  ,changci_quota
  ,remark
 from cs_campaign_changci p
WHERE
    p.tuid=${fld:id}
order by p.show_order