INSERT	INTO
t_form_item
(
	tuid
	, form_id
	, item_name_cn
	, item_name_en
	, col_num
	, show_order
	, remark
	, item_code
	, linkage_document_id
	, fieldset_style
	, plugin_top
	, plugin_bottom
)
VALUES
(
	${seq:nextval@seq_form_item}
	,${fld:form_id}
	,${fld:item_name_cn}
	,${fld:item_name_en}
	,${fld:col_num}
	,${fld:show_order}
	,${fld:remark}
	,${fld:item_code}
	,${fld:linkage_document_id}
	,${fld:fieldset_style}
	,${fld:plugin_top}
	,${fld:plugin_bottom}
)