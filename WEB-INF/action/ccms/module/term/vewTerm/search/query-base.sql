select 
	tuid,
	term_name,
	logo_path
 from 
 	t_term
 where
 	tenantry_id = ${def:tenantry}
 and
 	status = '1'
 	
 	${filter}
 	${orderby}