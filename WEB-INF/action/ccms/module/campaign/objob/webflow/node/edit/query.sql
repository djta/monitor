SELECT
	f.tuid
	, f.job_id
	, f.node_name
	, f.node_type
	, f.success_quota
	, f.paper_id
	, f.wait_time
	, f.ob_type
	, f.is_auto_assign
	, f.remark
	, f.grab_flag
	, f.grab_skip_flag
	, f.grab_flag_scope
	, te.term_name	as paper_name
	, f.sms_template_id
	, st.template_name  as sms_template_name
	, f.mms_template_id
	, mt.template_name as mms_template_name
	, f.email_template_id
	, et.template_name  as email_template_name
	, f.remind_template_id
	, rt.template_name  as remind_template_name
	, f.email_subject
	, f.email_send_type
	, f.remind_subject
	, f.dm_job_id
	, dj.job_name  as dm_job_name
	, f.node_width
	, f.node_height
	, f.node_x
	, f.node_y
	, f.limit_dial_count
FROM cs_job_node f
left join t_term te on f.paper_id = te.tuid
left join cc_sms_template st on f.sms_template_id = st.tuid
left join cc_mms_template mt on mt.tuid = cast(f.mms_template_id as char)
left join cc_email_template et on f.email_template_id = et.tuid
left join cc_remind_template rt on f.remind_template_id = rt.tuid
left join cc_dm_job dj on f.dm_job_id = dj.tuid
WHERE f.tuid=${fld:id}
