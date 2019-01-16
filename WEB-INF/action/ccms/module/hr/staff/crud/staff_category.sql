select
domain_value,
domain_text_cn
from
t_domain
where
 namespace = 'StaffCategory'
 and
 is_enabled='1'