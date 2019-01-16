INSERT	INTO
t_document
(
	tuid
	, subject_id
	, document_type
	, action_type
	, document_name
	, table_id
	, form_id
	, report_id
	, remark
	, nav_url
	, nav_url_width
	, url
	, nav_url_bottom
	, nav_bottom_hight
	, nav_url_right
	, nav_right_width
	, nav_url_top
	, nav_url_hight
	, doc_width
	, doc_hight
	,created
	,createdby
	,is_deleted
	,template_uri
)
VALUES
(
	${seq:nextval@seq_document}
	,${fld:subject_id}
	,${fld:document_type}
	,${fld:action_type}
	,${fld:document_name}
	,${fld:table_id}
	,${fld:form_id}
	,${fld:report_id}
	,${fld:remark}
	,${fld:nav_url}
	,${fld:nav_url_width}
	,${fld:url}
	,${fld:nav_url_bottom}
	,${fld:nav_bottom_hight}
	,${fld:nav_url_right}
	,${fld:nav_right_width}
	,${fld:nav_url_top}
	,${fld:nav_url_hight}
	,${fld:doc_width}
	,${fld:doc_hight}
	,{ts '${def:timestamp}'}
	,'${def:user}'
	,'0'
	,${fld:template_uri}
)