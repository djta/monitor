SELECT
	t.org_id
	,t.org_name
	,t.remark
	,t.org_grade
	,t.org_code
	,t.memberhead
	,t.show_order
	,t.short_name
	,t.org_type
	,null as org_type_name
	,i.contact_phone
	,i.province
	,i.city
	,i.district
	,i.business_hours_begin
	,i.business_hours_end
	,i.business_hours_type
	,(
		select f.tuid from t_attachment_files f 
		where f.table_code = 'hr_org' and f.pk_value = t.org_id::varchar 
		order by tuid desc limit 1
	) as upload_id 
FROM hr_org t 
LEFT JOIN hr_org_info i ON t.org_id = i.org_id 
WHERE t.org_id = ${fld:id}
