select
	p.tuid
 	,p.changci_name
 	,p.changci_time
 	,p.regist_to_time
 	,p.regist_from_time as start_time
 	,p.regist_to_time as end_time
 	,(select max(domain_text_cn) from t_domain where namespace='City' and domain_value=p.event_city) as event_city
 	,(select max(domain_text_cn) from t_domain where namespace='VehicleSeries' and domain_value=p.car_series) as car_series
from 
	cs_campaign_changci p
WHERE
	p.campaign_id=${fld:campaign_id}
${filter}
order by 
	p.regist_to_time desc
