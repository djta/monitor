SELECT
	d.tuid as document_id
	,d.subject_id
	,d.form_id
	,t.tuid as table_id
	,d.document_name
	,d.action_type
	,d.report_id
	,d.url
	,case when d.doc_width is null then '100%' else d.doc_width end as doc_width
	,case when d.doc_hight is null then '100%' else d.doc_hight end as doc_hight
	,d.nav_url
	,case when d.nav_url_width is null then '0' else d.nav_url_width end as nav_url_width
	,d.nav_url_right
	,case when d.nav_right_width is null then '0' else d.nav_right_width end as nav_right_width
	,d.nav_url_top
	,case when d.nav_url_hight is null then '0' else d.nav_url_hight end as nav_url_hight
	,d.nav_url_bottom
	,case when d.nav_bottom_hight is null then '0' else d.nav_bottom_hight end as nav_bottom_hight
FROM
	t_document d
	left join t_form f on f.tuid = d.form_id
	left join t_table t on f.table_id=t.tuid
WHERE
	d.tuid = ${document_id}
