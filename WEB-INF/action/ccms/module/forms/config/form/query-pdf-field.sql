select 
	t.tuid    as  field_id
	, t.field_code
	, t.field_code_alias
	, concat((case when is_virtual_type='0' then '' else '@' end) , t.field_name) as alias

	,case when p.show_order is null then t.show_order else p.show_order end   as  show_order
	,p.format
	,p.show_type
	,case when p.width is null then 0 else p.width end   as  width
from 
	t_form fm
	inner join t_field t
	on t.table_id = fm.table_id
	inner join (select a.field_id,a.show_order,a.format,a.show_type,a.width from t_form_pdf_field a where a.form_id = ${fld:form_id}) p on t.tuid=p.field_id
where 
	fm.tuid = ${fld:form_id}
order by 
	p.show_order
