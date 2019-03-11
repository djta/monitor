select
tuid
,concat(concat(name,'-'),ip) as name
from 
cc_device
where is_deleted is null