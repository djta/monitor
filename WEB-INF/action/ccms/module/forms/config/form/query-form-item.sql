SELECT
	f.tuid  as  item_id
	,f.item_name_${def:locale}    as  item_name
FROM
	t_form_item f
WHERE
	f.form_id=${fld:form_id}
