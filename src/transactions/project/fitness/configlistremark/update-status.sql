update cc_config set 
  	status= ${fld:status}
where
	tuid = ${fld:cnfg_id} and category = ${fld:category}
