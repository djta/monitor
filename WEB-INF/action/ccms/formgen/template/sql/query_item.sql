SELECT
    ff.tuid    as  item_id
    ,ff.item_name_cn as item_name
    ,ff.item_name_cn    as  item_name_cn
    ,ff.item_name_en    as  item_name_en
    ,ff.col_num      as  col_num
    ,ff.show_order    as  show_order
    ,ff.item_code
    ,ff.linkage_document_id
    ,ff.fieldset_style
    ,ff.plugin_top
    ,ff.plugin_bottom
FROM
	t_form f
	inner join t_form_item ff
	on ff.form_id = f.tuid
WHERE
    f.tuid = ${fld:form_id}
union
SELECT
    0    as  item_id
    ,'信息'    as  item_name
    ,'信息'    as  item_name_cn
    ,'Information'    as  item_name_en
    ,null      as  col_num
    ,10  as  show_order
    ,'' as item_code
    ,null as linkage_document_id
    ,'' as fieldset_style
    ,'' as plugin_top
    ,'' as plugin_bottom
FROM
	t_form f
WHERE
    f.tuid = ${fld:form_id}
order by 
	show_order