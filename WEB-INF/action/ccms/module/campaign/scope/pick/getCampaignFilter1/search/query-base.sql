select
c.tuid as campaign_id ,
c.campaign_name,
c.campaign_code,
case show_order when '1' then '全国活动' when '2' then '大区活动' when '3' then '省份活动' when '4' then '城市活动' when '5' then '经销商活动' when '6' then '品牌活动' when '7' then '车系活动' else '' end as event_type,
ta.scope_name,
concat('活动代码:',c.campaign_code,' ',c.event_summary,c.event_introduction,'(',c.begin_date,'至',c.end_date,')') as description
from (
select 
distinct 
 campaign_id,
 show_order,
 scope_name
from  (
select
  campaign_id,
  scope_type as show_order,
  concat('全国') as scope_name
from 
  cs_campaign_scope
where 
  scope_type='1' /*全国*/
and(
  (
  ${fld:validate_date}='0'
  and (
  cast(start_date as DATE)>= {d '${def:date}'} 
  or 
  cast(end_date as DATE)- 1 < {d '${def:date}'}
  )
  )
  or
  (
  ${fld:validate_date}='1'
  and cast(start_date as DATE)<= {d '${def:date}'} 
  and cast(end_date as DATE) - 1 >= {d '${def:date}'}
  )
  )    
  ${filter} 
  
)  as tt

) as ta 
inner join  cs_campaign c on  c.tuid= ta.campaign_id  and c.campaign_type = '0' AND c.campaign_status = '1' and 
case when ${fld:campaign_name} is not null then c.campaign_name else '1' end like  case when ${fld:campaign_name} is not null then concat('%',${fld:campaign_name},'%') else '1' end 
and (case when ${fld:is_recommend} is  null then '1' else is_recommend end )=( case when ${fld:is_recommend} is  null then '1' else ${fld:is_recommend} end )        
   
order by cast(show_order as SIGNED) desc,ta.scope_name  
