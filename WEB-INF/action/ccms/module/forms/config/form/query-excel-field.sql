select 
	t.tuid    as  field_id
	, t.field_code
	, t.field_code_alias
	, concat((case when is_virtual_type='0' then '' else '@' end) , t.field_name) as alias

	,case when e.show_order is null then t.show_order else e.show_order end   as  show_order
	,e.show_type
	,case when e.width is null then 0 else e.width end   as  width
from 
	t_form fm
	inner join t_field t
	on t.table_id = fm.table_id
	inner join (select a.field_id,a.show_order,a.show_type,a.width from t_form_excel_field a where a.form_id = ${fld:form_id})  e on t.tuid=e.field_id
where 
	fm.tuid = ${fld:form_id}
order by 
	e.show_order
