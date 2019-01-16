
select 
	d.domain_text_${def:locale} as domain_text
	,d.domain_value
from 
	t_domain d
	inner join cs_job_filter t
	on d.namespace = t.namespace
where 
	d.is_enabled = '1'
and
	(t.namespace = 'dropSolvedResult' or t.namespace = 'OutboundType' or t.namespace = 'IncidentType')
and
	t.tuid=${fld:id}

union

select
	t.campaign_name as domain_text
	,t.tuid as domain_value
from 
	cs_campaign t
where
	t.is_deleted != '1'
and
	(select namespace from cs_job_filter where tuid=${fld:id}) = 'campaign'