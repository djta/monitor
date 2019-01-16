select
	distinct
	gr.group_id
from
	${schema}s_role r 
inner join 
	${schema}s_service_group_role gr
on 
	r.role_id = gr.role_id
where
	r.role_id = ${fld:id}
