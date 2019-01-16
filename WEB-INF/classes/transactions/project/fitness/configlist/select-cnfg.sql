select count(1) as count from cc_config 
where org_id = ${def:org} 
	and category = ${fld:category}