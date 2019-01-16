union 
select
  s.campaign_id,
  s.scope_type as show_order,
  d.domain_text_${def:locale} as scope_name
from 
  cs_campaign_scope s
  inner join t_domain d on s.scope_value = d.domain_value and d.namespace='Province'
where 
  ${fld:province} is  not null 
  and s.scope_type='3'  /*省*/
  and s.scope_value= ${fld:province}
  and(
  	(${fld:validate_date}='0'
  and (
  	cast(start_date as DATE)>= {d '${def:date}'} 
  	or 
  	cast(end_date as DATE)- 1 < {d '${def:date}'}
  	))
  or
  	(
  	${fld:validate_date}='1'
  	and cast(s.start_date as DATE)<= {d '${def:date}'} 
  	and cast(s.end_date as DATE)- 1 >= {d '${def:date}'}
  	)
  )
  
  union
/*本省的经销商*/
select
  s.campaign_id,
  s.scope_type as show_order,
  d.dealer_name_${def:locale} as scope_name
from 
  cs_campaign_scope s
  inner join cc_dealer d on s.scope_value = d.dealer_code
where 
  ${fld:province}    is  not null 
  and ${fld:dealer} is null
  and s.scope_type='5'  /*经销商*/
  and(
  	(
  ${fld:validate_date}='0'
  and (
  	cast(s.start_date as DATE)> {d '${def:date}'} 
  or 
  	cast(s.end_date as DATE)- 1 < {d '${def:date}'}
  	)
  	)
  or
  (
  	${fld:validate_date}='1'
  	and cast(s.start_date as DATE)<= {d '${def:date}'} 
  	and cast(s.end_date as DATE)- 1 >= {d '${def:date}'}
  )
  )  and exists 
  (select 1 from cc_dealer d where d.dealer_code=s.scope_value and d.province=${fld:province} 
  and case when ${fld:city} is not null then d.city else '1' end = case when ${fld:city} is not null then ${fld:city} else '1' end 
  and case when ${fld:dealer_name} is not null then d.dealer_name_${def:locale} else '1' end like  case when ${fld:dealer_name} is not null then concat('%',${fld:dealer_name},'%') else '1' end
  )
