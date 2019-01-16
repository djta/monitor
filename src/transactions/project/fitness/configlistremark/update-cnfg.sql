update cc_config set 
  	param_value= ${fld:vc_topic},
  	param_text= ${fld:vc_content},
  	remark= ${fld:vc_remark},
  	updatedby = '${def:user}',
    updated = {ts'${def:timestamp}'}
where
	tuid = ${fld:cnfg_id}
