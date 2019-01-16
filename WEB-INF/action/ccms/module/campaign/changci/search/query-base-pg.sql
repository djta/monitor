select
	p.tuid
 	,p.changci_name
 	,p.changci_time as description
 	,p.regist_to_time
 	,to_char(p.regist_from_time,'yyyy-MM-dd hh24:mi') as start_time
 	,to_char(p.regist_to_time,'yyyy-MM-dd hh24:mi') as end_time
 	,(select max(domain_text_cn) from t_domain where namespace='City' and domain_value=p.event_city) as event_city
 	,(select max(domain_text_cn) from t_domain where namespace='VehicleSeries' and domain_value=p.car_series) as car_series
from 
	cs_campaign_changci p
WHERE
	p.campaign_code=${fld:campaign_id}
${filter}
order by 
	p.regist_to_time desc
