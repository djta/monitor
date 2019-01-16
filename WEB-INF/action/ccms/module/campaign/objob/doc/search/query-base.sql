select
	f.tuid
	,t.tuid as  document_id
	,t.document_name
	,case when f.form_type='0' then '客户信息' when f.form_type='1' then '快速新增' else '其他' end  as  form_type
	,f.show_order
	from
	cs_job_form f
	inner join t_document t on f.document_id=t.tuid 
	WHERE 
	1=1

${filter}
