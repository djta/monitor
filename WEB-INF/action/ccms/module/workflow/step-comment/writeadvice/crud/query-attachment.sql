select
    t.tuid,
    t.file_name,
    t.description
from 
    t_attachment_files t 
where
    t.pk_value = ${fld:entry_id}
and
    t.table_id = ${fld:node_id}