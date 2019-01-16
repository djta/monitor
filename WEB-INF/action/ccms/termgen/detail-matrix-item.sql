select  
    tuid
    ,item_id
    ,item_name
    ,item_code
    ,show_order
    ,show_type
    ,remark  
from 
    t_term_item_matrix 
where 
	item_id = ${item_id}

order by show_order asc

