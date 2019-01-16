select * 
from t_attachment_files 
where tuid = ${fld:tuid} 
order by tuid desc limit 1
