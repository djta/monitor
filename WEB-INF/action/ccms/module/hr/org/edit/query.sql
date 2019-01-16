SELECT
	t.org_id
	,t.org_name
	,t.remark
	,t.org_grade
	,t.show_order
	,t.short_name
	,t.org_type
	,i.business_hours_begin
	,i.business_hours_end
	,i.business_hours_type
	,(select tuid from t_attachment_files where table_code='hr_org' and pk_value=t.org_id::varchar order by tuid desc limit 1) as upload_id

FROM
	hr_org t 
LEFT JOIN hr_org_info i ON t.org_id = i.org_id 
WHERE
	t.org_id = ${fld:id}