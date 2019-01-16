SELECT 1 
FROM cc_app_version 
WHERE version_no = ${fld:version_no} 
AND app_code = ${fld:app_code} 
AND is_deleted = 0
