select
	cs_campaign_id
	,cs_job_id
	,next_node
	,email_skill_id
	,sms_skill_id
	,remind_skill_id
	,email_skill_subject
	,remind_skill_subject
	,email_skill_template_id
	,sms_skill_template_id
	,remind_skill_template_id
	,email_assign_user
	,sms_assign_user
	,remind_assign_user
from	
	os_wfm_node_to a
where
	tuid = ${action_id}