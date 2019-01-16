select * 
from t_attachment_files 
where pk_value = ${fld:pk_value} and table_code = ${fld:table_code} 
and org_id = ${def:org} 
order by tuid desc limit 1
