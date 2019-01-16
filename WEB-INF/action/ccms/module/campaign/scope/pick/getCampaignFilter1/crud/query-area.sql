select
domain_text_cn as area_name 
,domain_value as area_code
from
t_domain
where
namespace='Area'
and
is_enabled='1'