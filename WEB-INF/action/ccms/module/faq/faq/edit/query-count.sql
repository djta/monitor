select 
    case when (select count(1) from t_faq f where f.superior_id = ${fld:tuid} and f.is_delete != '1' )=0 then 'block' 
            else 'none' end  as delete_button_style 
from 
	dual