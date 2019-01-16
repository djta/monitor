INSERT INTO os_wfm_node_to
(
	tuid
	, node_id
	, next_node
	, action_name
	, is_auto
	, pre_class
	, post_class
	, condition_class
	, authority_id
	, quartz_delay
	, quartz_cron
	, quartz_class
	, quartz_timeout
	, email_skill_id
	, email_skill_subject
	, email_skill_template_id
	, sms_skill_id
	, sms_skill_template_id
	, remind_skill_id
	, remind_skill_subject
	, remind_skill_template_id
	, email_assign_user
	, sms_assign_user
	, remind_assign_user
)
VALUES
(
	  ${fld:tuid}
	, ${fld:node_id}
	, ${fld:next_node}
	, ${fld:action_name}
	, ${fld:is_auto}
	, ${fld:pre_class}
	, ${fld:post_class}
	, ${fld:condition_class}
	, ${fld:authority_id}
	, ${fld:quartz_delay}
	, ${fld:quartz_cron}
	, ${fld:quartz_class}
	, ${fld:quartz_timeout}
	, ${fld:email_skill_id}
	, ${fld:email_skill_subject}
	, ${fld:email_skill_template_id}
	, ${fld:sms_skill_id}
	, ${fld:sms_skill_template_id}
	, ${fld:remind_skill_id}
	, ${fld:remind_skill_subject}
	, ${fld:remind_skill_template_id}
	, ${fld:email_assign_user}
	, ${fld:sms_assign_user}
	, ${fld:remind_assign_user}
)
