select
	concat('c' , t.tuid) as tuid,
	t.campaign_name as show_name,
	'0' as superior_id,
	'parent' as operator,
	'' as super,
	'' as addon,
	'1' as show_order
from cs_campaign t
where t.tuid = ${fld:campaign_id}  
and t.is_deleted != '1' 
	
union

select
	concat('j' , t.tuid) as tuid,
	t.job_name as show_name,
	concat('c' , t.campaign_id) as superior_id,
	'campaign' as operator,
	'' as super,
	'' as addon,
	cast((case when t.job_priority is null then 0 else t.job_priority end + 70) as char) as show_order
from cs_job t
inner join cs_campaign c on t.campaign_id = c.tuid and c.is_deleted != '1'
where t.parent_id is null 
and t.campaign_id = ${fld:campaign_id}

union

select
	concat('j' , t.tuid) as tuid,
	t.job_name as show_name,
	concat('j' , t.parent_id) as superior_id,
	'parentjob' as operator,
	concat('c' , t.campaign_id) as super,
	'' as addon,
	cast((case when t.job_priority is null then 0 else t.job_priority end + 80) as char) as show_order
from  cs_job t
inner join cs_campaign c on t.campaign_id = c.tuid and c.is_deleted != '1'
where t.parent_id is not null
and t.campaign_id = ${fld:campaign_id}

union

select
	concat('y' , t.tuid) as tuid,
	'流转节点' as show_name,
	concat('j' , t.tuid) as superior_id,
	'job' as operator,
	'' as super,
	'' as addon,
	'5' as show_order
from cs_job t
inner join cs_campaign c on t.campaign_id = c.tuid and c.is_deleted != '1'
where t.campaign_id = ${fld:campaign_id}

union

(	
select
	concat('n' , t.tuid) as tuid,
	t.node_name as show_name,
	concat('y' , t.job_id) as superior_id,
	'job' as operator,
	'' as super,
	'' as addon,
	'9' as show_order
from cs_job_node t
inner join cs_job j on t.job_id=j.tuid
and j.campaign_id = ${fld:campaign_id}
order by t.node_type
)

union

select
	concat('s' , t.tuid) as tuid,
	'人群定义' as show_name,
	concat('j' , t.tuid) as superior_id,
	'' as operator,
	'' as super,
	'' as addon,
	'1' as show_order
from  cs_job t
inner join cs_campaign c on t.campaign_id = c.tuid
and c.is_deleted != '1' and t.campaign_id = ${fld:campaign_id} 

union

select
	concat('e' , t.tuid) as tuid,
	'活动限定人群' as show_name,
	concat('s' , t.tuid) as superior_id,
	'clause' as operator,
	concat('r' , t.tuid) as super,
	'1' as addon,
	'1' as show_order
from cs_job t
inner join cs_campaign c on t.campaign_id = c.tuid 
and c.is_deleted != '1' and t.campaign_id = ${fld:campaign_id}

union

select
	concat('g' , t.tuid) as tuid,
	'活动排除人群' as show_name,
	concat('s' , t.tuid) as superior_id,
	'clause' as operator,
	concat('r' , t.tuid) as super,
	'2' as addon,
	'2' as show_order
from cs_job t
inner join cs_campaign c on t.campaign_id = c.tuid 
and c.is_deleted != '1' and t.campaign_id = ${fld:campaign_id}

union

select
	concat('r' , t.tuid) as tuid,
	'人群限定定义' as show_name,
	concat('s' , t.tuid) as superior_id,
	'clause' as operator,
	concat('r' , t.tuid) as super,
	'0' as addon,
	'3' as show_order
from cs_job t
inner join cs_campaign c on t.campaign_id = c.tuid 
and c.is_deleted != '1' and t.campaign_id = ${fld:campaign_id}

union

select
	concat('h' , t.tuid) as tuid,
	'人群追加定义' as show_name,
	concat('s' , t.tuid) as superior_id,
	'clause' as operator,
	concat('r' , t.tuid) as super,
	'3' as addon,
	'4' as show_order
from cs_job t
inner join cs_campaign c on t.campaign_id = c.tuid
and c.is_deleted != '1' and t.campaign_id = ${fld:campaign_id}

union

select
	concat('f' , t.tuid) as tuid,
	case when t.logic_type='and' then '同时满足条件的结果'
		when t.logic_type='or' then '任意满足其中一个条件的结果' 
		else '' end as show_name,
	case when t.parent_id is null then 
		concat(case when t.filter_type='1' then 'e'
			     when t.filter_type='2' then 'g'
			     when t.filter_type='3' then 'h'
			     else 'r' end
			, t.job_id) 
		else concat('f' , t.parent_id) end as superior_id,
	'clause-node' as operator,
	concat('r' , t.job_id) as super,
	t.filter_type as addon,
	'9' as show_order
from cs_job_filter t
inner join cs_job j on t.job_id=j.tuid and j.campaign_id = ${fld:campaign_id}
where t.is_node = '0'

union

select
	concat('f' , t.tuid) as tuid,
	substring(concat(d.field_name_${def:locale} 
	, '&nbsp;' , 
	case when t.clause_filter='=' then '等于'
	     when t.clause_filter='like' then '包含'
	     when t.clause_filter='>' then '大于'
	     when t.clause_filter='>=' then '大于等于'
	     when t.clause_filter='<' then '小于'
	     when t.clause_filter='<=' then '小于等于'
	     when t.clause_filter='<>' then '不等于'
	     when t.clause_filter='is not null' then '非空'
	     when t.clause_filter='is null' then '为空'
	     when t.clause_filter='in' then '任意满足'
	     when t.clause_filter='not in' then '排除'
	     else '' end
	, '&nbsp;' , 
	case when t.phrase_name is null then '' else t.phrase_name end),1,50) as show_name,
	case when t.parent_id is null then 
		concat(case when t.filter_type='3' then 'h'
			     else 'r' end
			, t.job_id) 
		else concat('f' , t.parent_id) end as superior_id,
	'clause-item' as operator,
	concat('r' , t.job_id) as super,
	t.filter_type as addon,
	'9' as show_order
from cs_job_filter t
inner join cs_job j on t.job_id=j.tuid and j.campaign_id = ${fld:campaign_id}
left join t_form f on t.form_id = f.tuid
left join t_field d on (f.table_id=d.table_id and t.clause_code=d.field_code)
where t.is_node = '1'
and (t.filter_type='0' or t.filter_type='3')

union

select
	concat('f' , t.tuid) as tuid,
	substring(concat(case when t.clause_code='cc_incident.campaign_id' then '活动编号'
		when t.clause_code='cc_incident.incident_type' then '沟通类型'
		when t.clause_code='cc_incident.talk_code' then '沟通结果'
		when t.clause_code='cc_incident.call_code' then '拨打状态'
		when t.clause_code='cc_incident.mb_subject' then '外呼主题'
		when t.clause_code='cc_incident.agent_update_time' then '拨打时间'
		when t.clause_code='cs_task_pool.is_done' then '是否完成活动'
	end
	, '&nbsp;' , 
	case when t.clause_filter='=' then '等于'
	     when t.clause_filter='like' then '包含'
	     when t.clause_filter='>' then '大于'
	     when t.clause_filter='>=' then '大于等于'
	     when t.clause_filter='<' then '小于'
	     when t.clause_filter='<=' then '小于等于'
	     when t.clause_filter='<>' then '不等于'
	     when t.clause_filter='is not null' then '非空'
	     when t.clause_filter='is null' then '为空'
	     when t.clause_filter='in' then '任意满足'
	     when t.clause_filter='not in' then '排除'
	     else '' end
	, '&nbsp;' ,
	case when t.phrase_name is null then '' else t.phrase_name end),1,50) as show_name,
	case when t.parent_id is null then 
		concat(case when t.filter_type='1' then 'e'
			     when t.filter_type='2' then 'g' end
			, t.job_id)  
		else concat('f' , t.parent_id) end as superior_id,
	'clause-item' as operator,
	concat('r' , t.job_id) as super,
	t.filter_type as addon,
	'9' as show_order
from cs_job_filter t
inner join cs_job j on t.job_id=j.tuid and j.campaign_id = ${fld:campaign_id}
where t.is_node = '1'
and (t.filter_type='1' or t.filter_type='2')
	
order by show_order,show_name
