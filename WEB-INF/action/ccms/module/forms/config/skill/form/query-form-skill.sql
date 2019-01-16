select
	tuid as form_id
	,oper_priviledge
from
	t_form
where
	tuid = ${fld:form_id}