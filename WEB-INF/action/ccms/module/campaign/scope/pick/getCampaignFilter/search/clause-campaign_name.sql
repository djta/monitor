union
select
  s.campaign_id,
  s.scope_type as show_order,
  case s.scope_type when '1' then '全国' when '2' then t1.domain_text_${def:locale} when '3' then d.dealer_name_${def:locale} when '4' then t3.domain_text_${def:locale} when '5' then d.dealer_name_${def:locale} when '6' then t4.domain_text_${def:locale} when '7' then t5.domain_text_${def:locale} else '' end as scope_name
from 
  cs_campaign_scope s
  inner join cs_campaign c on s.campaign_id = c.tuid
  left join cc_dealer d on s.scope_value = d.dealer_code and s.scope_type='5'
  left join t_domain t1 on s.scope_value = t1.domain_value and s.scope_type='2' and t1.namespace='Area'
  left join t_domain t2 on s.scope_value = t2.domain_value and s.scope_type='3' and t1.namespace='Provine'
  left join t_domain t3 on s.scope_value = t3.domain_value and s.scope_type='4' and t1.namespace='City'
  left join t_domain t4 on s.scope_value = t4.domain_value and s.scope_type='6' and t1.namespace='IntendedBrand'
  left join t_domain t5 on s.scope_value = t5.domain_value and s.scope_type='7' and t1.namespace='IntenedeSeries'
where 

  case when ${fld:campaign_name} is not null then c.campaign_name else '1' end like  case when ${fld:campaign_name} is not null then concat('%',${fld:campaign_name},'%') else '0' end
  and(
  (
  ${fld:validate_date}='0'
  and (
  cast(c.begin_date as DATE)> {d '${def:date}'} 
  or 
  cast(c.end_date as DATE)- 1 < {d '${def:date}'}
  )
  )
  or
  (
  ${fld:validate_date}='1'
  and cast(c.begin_date as DATE)<= {d '${def:date}'} 
  and cast(c.end_date as DATE)- 1 >= {d '${def:date}'}
  )
  )
  