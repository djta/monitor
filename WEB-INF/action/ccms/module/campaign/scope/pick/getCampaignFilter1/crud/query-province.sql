select
domain_text_cn as province_name 
,domain_value as province_code
from
t_domain
where
namespace='Province'
and
is_enabled='1'