select
  tuid
  ,campaign_code as campaign_id
  ,(select  campaign_name from cs_campaign where tuid=p.campaign_id) as campaign_name    
  ,changci_name
  ,changci_time
  ,to_char(regist_from_time,'yyyy-MM-dd hh24:mi') as regist_from_time
  ,to_char(regist_from_time,'yyyy-MM-dd') as regist_from_time_date
  ,to_char(regist_from_time,'hh24') as regist_from_time_hour
  ,to_char(regist_from_time,'mi') as regist_from_time_minute
  ,to_char(regist_to_time,'yyyy-MM-dd hh24:mi') as regist_to_time
  ,to_char(regist_to_time,'yyyy-MM-dd') as regist_to_time_date
  ,to_char(regist_to_time,'hh24') as regist_to_time_hour
  ,to_char(regist_to_time,'mi') as regist_to_time_minute
  ,event_city
  ,car_series
  ,show_order
  ,changci_quota
  ,remark
 from cs_campaign_changci p
WHERE
    p.tuid=${fld:id}
order by p.show_order