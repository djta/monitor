select
	distinct
	*
from
(
select
	distinct
	t.*
from 
	t_table t
	inner join t_form f on t.tuid = f.table_id
	inner join t_document d on f.tuid = d.form_id
where 
	d.tuid = ${fld:document_id}
union

select
	distinct
	t.*
from 
	t_table t
	inner join t_report f on t.tuid = f.table_id
	inner join t_document d on f.tuid = d.report_id
where 
	d.tuid = ${fld:document_id}
) t