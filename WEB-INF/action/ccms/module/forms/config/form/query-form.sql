select 
	 case when  col_num_edit is null or col_num_edit=0 then 12  else  12/col_num_edit  end as col_num_edit
	, case when  col_num_filter is null or col_num_filter=0 then 12 else 12/col_num_filter end as col_num_filter
from 
	t_form fm
where 
	fm.tuid = ${fld:form_id}
