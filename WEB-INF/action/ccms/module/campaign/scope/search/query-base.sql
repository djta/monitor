select
concat('<input type="checkbox" class="duoxuan" name="scope_id" value="',s.tuid,'">' ) as application_id,
	s.tuid
 	,c.campaign_name
 	,case s.scope_type when '1' then '全国' when '2' then '大区' when '3' then '省' when '4' then '市' when '5' then '经销商' when '6' then '品牌' when '7' then '车系' else '' end as scope_type
 	,case s.scope_type when '1' then '' when '2' then t2.domain_text when '3' then t3.domain_text when '4' then t4.domain_text when '5' then t5.dealer_name when '6' then t6.domain_text when '7' then t7.domain_text else '' end as scope_value
 	,s.start_date
 	,s.end_date
 	,s.created
 	,f.name as createdby
from 
	cs_campaign_scope s 
	join cs_campaign c on c.tuid=s.campaign_id
	left join (select domain_value,domain_text_${def:locale} as domain_text,'2'as scope_type from t_domain where namespace='IntendedBrand' and is_enabled='1') t2 on s.scope_type=t2.scope_type and t2.domain_value=s.scope_value
	left join (select domain_value,domain_text_${def:locale} as domain_text,'3' as scope_type from t_domain where namespace='Province' and is_enabled='1') t3 on s.scope_type=t3.scope_type and t3.domain_value=s.scope_value
	left join (select domain_value,domain_text_${def:locale} as domain_text,'4' as scope_type from t_domain where namespace='City' and is_enabled='1') t4 on s.scope_type=t4.scope_type and t4.domain_value=s.scope_value
	left join (select dealer_code,dealer_name_${def:locale} as dealer_name,'5' as scope_type from cc_dealer) t5 on s.scope_type=t5.scope_type and t5.dealer_code=s.scope_value
	left join (select domain_value,domain_text_${def:locale} as domain_text,'6' as scope_type from t_domain where namespace='IntendedBrand' and is_enabled='1') t6 on s.scope_type=t6.scope_type and t6.domain_value=s.scope_value
	left join (select domain_value,domain_text_${def:locale} as domain_text,'7' as scope_type from t_domain where namespace='IntenedeSeries' and is_enabled='1') t7 on s.scope_type=t7.scope_type and t7.domain_value=s.scope_value
	left join hr_staff f on f.userlogin=s.createdby
WHERE
    s.campaign_id=${fld:campaign_id}
${filter}
order by 
	s.created desc
