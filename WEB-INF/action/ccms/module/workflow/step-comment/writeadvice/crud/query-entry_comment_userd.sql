select 
   comments as usedadvice
from 
  os_entry_common_used
where 
   createdby ='${def:user}'
  and
   is_deleted='0'