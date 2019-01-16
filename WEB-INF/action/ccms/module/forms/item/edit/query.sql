SELECT
    t.tuid
    ,t.form_id
    ,t.item_name_cn
    ,t.item_name_en
    ,t.col_num
    ,t.show_order
    ,t.remark
	,t.item_code  
	,t.linkage_document_id
	,f.document_name as linkage_document_alias
	,t.fieldset_style
	,t.plugin_top
	,t.plugin_bottom
FROM
	t_form_item t
	left join t_document f on f.tuid = t.linkage_document_id
WHERE
	t.tuid=${fld:id}
