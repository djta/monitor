UPDATE
	t_document
SET
	document_type     =${fld:document_type}
	,action_type     =${fld:action_type}
	,document_name     =${fld:document_name}
	,table_id    =${fld:table_id}
	,form_id    =${fld:form_id}
	,report_id = ${fld:report_id}
	,remark	 =${fld:remark}
	,nav_url	 =${fld:nav_url}
	,nav_url_width	 =${fld:nav_url_width}
	,url = ${fld:url}
	,nav_url_bottom = ${fld:nav_url_bottom}
	,nav_bottom_hight = ${fld:nav_bottom_hight}
	,nav_url_right = ${fld:nav_url_right}
	,nav_right_width = ${fld:nav_right_width}
	,nav_url_top = ${fld:nav_url_top}
	,nav_url_hight = ${fld:nav_url_hight}
	,doc_width = ${fld:doc_width}
	,doc_hight = ${fld:doc_hight}
	,updated = {ts '${def:timestamp}'}
	,updatedby = '${def:user}'
	,template_uri = ${fld:template_uri}
WHERE
	tuid	=${fld:tuid}
