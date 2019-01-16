select
	${seq:currval@seq_faq} as seq
	/*
	,(with RECURSIVE faq as ( 
		select a.tuid,a.show_name,a.superior_id from t_faq a where a.tuid=${fld:superior_id}
		union all  
		select k.tuid,k.show_name,k.superior_id  from t_faq k inner join faq c on c.superior_id = k.tuid
		)select array_agg(show_name) from faq ) 
	as superior
	*/
	,'' as superior
	,{ts '${def:timestamp}'} as create_date
from
	dual
