UPDATE security.s_user 
SET 
	enabled = ${enabled}
WHERE userlogin = ${fld:userlogin}
