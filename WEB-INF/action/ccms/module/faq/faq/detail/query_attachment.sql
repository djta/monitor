select
    tuid as attachment_id,
    file_name,
    file_type,
    (file_size / 1024) as attachment_size,
    description
from 
    t_attachment_files t 
where
    t.table_code = 't_faq'
and
    t.pk_value = ${fld:faq_id}
