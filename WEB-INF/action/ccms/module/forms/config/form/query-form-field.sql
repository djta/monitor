select 
	t.tuid    as  field_id
	, t.field_code
	, t.field_code_alias
	, concat(concat((case when i.item_name_${def:locale} is null then '' else concat(concat('(',i.item_name_${def:locale}),')') end),(case when is_virtual_type='0' then '' else '@' end) ) , t.field_name) as alias

	,case when s.show_order is null then t.show_order else s.show_order end as  show_order
	,s.show_type
	,case when s.is_mandatory is null then '1' else s.is_mandatory end  as  is_mandatory
	,case when s.is_readonly is null then '0' else s.is_readonly end   as  is_readonly
	,case when s.is_attachment is null then '0' else s.is_attachment end   as  is_attachment
	,s.item_id
	,s.show_color
	,case when s.width is null then 0 else s.width end    as  width
	,case when s.colspan is null then 1 else s.colspan end   as  colspan
	,case when s.rowspan is null then 1 else s.rowspan end   as  rowspan
	,case when s.is_cascade_combo is null then '0' else s.is_cascade_combo end as is_cascade_combo
from 
	t_form fm
	inner join t_field t on t.table_id = fm.table_id
	inner join (select a.field_id,a.is_mandatory,a.is_readonly,a.is_attachment,a.show_order,a.show_type,a.item_id,a.show_color,a.width,a.colspan,a.rowspan,a.is_cascade_combo from t_form_show_field a where a.form_id = ${fld:form_id}) s 	on t.tuid=s.field_id
	left join t_form_item i on s.item_id = i.tuid
	
where 
	fm.tuid = ${fld:form_id}
order by 
	s.show_order
