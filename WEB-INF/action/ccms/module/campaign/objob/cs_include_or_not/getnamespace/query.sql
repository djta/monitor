select 
	d.domain_text_${def:locale} as domain_text
	,d.domain_value
from 
	t_domain d
where 
	lower(d.namespace) = lower(${fld:namespace})
and
	d.is_enabled = '1'
and
	(${fld:namespace} = 'dropSolvedResult' or ${fld:namespace} = 'OutboundType' or ${fld:namespace} = 'IncidentType' or ${fld:namespace} = 'Optional')

union

select
	t.campaign_name as domain_text
	,t.tuid as domain_value
from 
	cs_campaign t
where
	t.is_deleted != '1'
and
	${fld:namespace} = 'campaign'