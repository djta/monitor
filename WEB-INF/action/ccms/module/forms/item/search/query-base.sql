SELECT
    t.tuid
    , t.item_name_cn
    , t.item_name_en
    , t.col_num
    , t.show_order
    , t.remark
    , t.item_code
    , d.document_name as linkage_document_alias
FROM
	T_FORM_ITEM t
	left join t_document d on d.tuid = t.linkage_document_id
WHERE
    1 = 1
and 
     t.form_id = ${fld:form_id} 
${filter}
${orderby}
