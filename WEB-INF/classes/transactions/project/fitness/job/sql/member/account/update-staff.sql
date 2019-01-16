UPDATE hr_staff 
SET 
	status = ${status}
WHERE userlogin = ${fld:userlogin}
