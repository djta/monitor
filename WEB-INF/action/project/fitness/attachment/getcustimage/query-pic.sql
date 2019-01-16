SELECT 
	tuid AS upload_id 
FROM t_attachment_files 
WHERE table_code = 'e_customer' AND pk_value = ${fld:vc_code} 
and org_id = ${def:org} 
ORDER BY tuid DESC LIMIT 1
