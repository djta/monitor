select
    t.tuid as attachment_id,
    t.file_name,
    t.file_type,
    (t.file_size / 1024) as attachment_size,
    t.description
from 
    t_attachment_files t
where
    t.table_code = 't_faq'
and
    t.pk_value = '${fld:faq_id}'
