INSERT	INTO
    cs_job_node
(
	tuid
	, job_id
	, node_name
	, node_type
	, success_quota
	, paper_id
	, ob_type
	, is_auto_assign
	, wait_time   
	, remark
	, grab_flag
	, grab_skip_flag
	, grab_flag_scope
	, sms_template_id
	, mms_template_id
	, email_template_id
	, email_subject
	, email_send_type
	, remind_template_id
	, remind_subject
	, dm_job_id
	, created
	, createdby
	, updated
	, updatedby
	, node_width
	, node_height
	, node_x
	, node_y
	, limit_dial_count
)
VALUES
(
	${seq:nextval@seq_cs_job_node}
	, ${fld:job_id}
	, ${fld:node_name}
	, ${fld:node_type}
	, ${fld:success_quota}
	, ${fld:paper_id}
	, ${fld:ob_type}
	, ${fld:is_auto_assign}
	, ${fld:wait_time}   
	, ${fld:remark}
	, ${fld:grab_flag}
	, ${fld:grab_skip_flag}
	, ${fld:grab_flag_scope}
	, ${fld:sms_template_id}
	, ${fld:mms_template_id}
	, ${fld:email_template_id}
	, ${fld:email_subject}
	, ${fld:email_send_type}
	, ${fld:remind_template_id}
	, ${fld:remind_subject}
	, ${fld:dm_job_id}
	, {ts '${def:timestamp}'}
	, '${def:user}'
	, {ts '${def:timestamp}'}
	, '${def:user}'
	, ${fld:node_width}
	, ${fld:node_height}
	, ${fld:node_x}
	, ${fld:node_y}
	, ${fld:limit_dial_count}
)
