select 
	t.tuid    as  field_id
	,t.field_code
	, t.field_code_alias
	,concat((case when is_virtual_type='0' then '' else '@' end) , t.field_name) as alias
	,case when g.show_order is null then t.show_order else g.show_order end   as  show_order
	,case when g.sort_order is null then '' else g.sort_order end   as  sort_order
	,g.show_flag
	,g.format
	,g.print_html
	,g.compute_total
	,g.show_align
from 
	t_form fm
	inner join t_field t
	on t.table_id = fm.table_id
	inner join (select a.field_id,a.show_order,a.show_flag,a.format,a.width,a.sort_order,a.print_html,a.compute_total,a.show_align from t_form_grid_field a where a.form_id = ${fld:form_id})  g 
	on t.tuid=g.field_id
where 
	fm.tuid = ${fld:form_id}
order by 
	g.show_order
