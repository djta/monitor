select 
	t.tuid    as  field_id
	, t.field_code
	, t.field_code_alias
	, concat((case when is_virtual_type='0' then '' else '@' end) , t.field_name) as alias

	,case when f.show_order is null then t.show_order else f.show_order end  as  show_order
	,f.show_type
	,case when f.width is null then 0 else f.width end   as  width
	,case when f.colspan is null then 1 else f.colspan end   as  colspan
	,case when f.rowspan is null then 1 else f.rowspan end   as  rowspan
	,case when f.is_mandatory is null then '1' else f.is_mandatory end  as  is_mandatory
	,f.filter_type as filter_type
	,f.item_id
from 
	t_form fm
	inner join t_field t
	on t.table_id = fm.table_id
	inner join (select a.field_id,a.show_order,a.show_type,a.item_id,a.width,a.filter_type,a.colspan,a.rowspan,a.is_mandatory from t_form_filter_field a where a.form_id = ${fld:form_id})  f 
	on t.tuid=f.field_id
where 
	fm.tuid = ${fld:form_id}
order by 
	f.show_order
