UPDATE
	t_form_item
SET
	form_id     =${fld:form_id}
	,item_name_cn     =${fld:item_name_cn}
	,item_name_en     =${fld:item_name_en}
	,col_num     =${fld:col_num}
	,show_order     =${fld:show_order}
	,remark	 =${fld:remark}
	,item_code	 =${fld:item_code}
	,linkage_document_id	 =${fld:linkage_document_id}
	,fieldset_style	 =${fld:fieldset_style}
	,plugin_top	 =${fld:plugin_top}
	,plugin_bottom	 =${fld:plugin_bottom}
WHERE
	tuid	=${fld:tuid}
