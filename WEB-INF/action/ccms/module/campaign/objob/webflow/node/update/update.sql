UPDATE
    cs_job_node
SET
	 node_name         = ${fld:node_name}    
	, node_type        = ${fld:node_type}    
	, success_quota        = ${fld:success_quota}    
	, paper_id      = ${fld:paper_id}  
	, wait_time        = ${fld:wait_time}  
	, ob_type        = ${fld:ob_type}  
	, is_auto_assign        = ${fld:is_auto_assign}  
	, remark            = ${fld:remark}        
	, grab_flag            = ${fld:grab_flag}        
	, grab_skip_flag            = ${fld:grab_skip_flag}        
	, grab_flag_scope            = ${fld:grab_flag_scope}     
	, sms_template_id            = ${fld:sms_template_id}      
	, mms_template_id	     = ${fld:mms_template_id}
	, email_template_id            = ${fld:email_template_id}        
	, email_subject            = ${fld:email_subject}        
	, email_send_type            = ${fld:email_send_type}
	, remind_template_id            = ${fld:remind_template_id}        
	, remind_subject            = ${fld:remind_subject}        
	, dm_job_id            = ${fld:dm_job_id}
	, updated	= {ts '${def:timestamp}'}
	, updatedby	= '${def:user}'
	, node_width = ${fld:node_width}
	, node_height = ${fld:node_height}
	, node_x = ${fld:node_x}
	, node_y = ${fld:node_y}
	, limit_dial_count = ${fld:limit_dial_count}
WHERE
	tuid	= ${fld:tuid}
