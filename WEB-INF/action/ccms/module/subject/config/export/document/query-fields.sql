select
	distinct
	*
from
(
select
	distinct
	fi.*
from 
	t_table t
	inner join t_field fi on fi.table_id = t.tuid
	inner join t_form f on t.tuid = f.table_id
	inner join t_document d on f.tuid = d.form_id
where 
	d.tuid = ${fld:document_id}
union

select
	distinct
	fi.*
from 
	t_table t
	inner join t_field fi on fi.table_id = t.tuid
	inner join t_report f on t.tuid = f.table_id
	inner join t_document d on f.tuid = d.report_id
where 
	d.tuid = ${fld:document_id}
) t