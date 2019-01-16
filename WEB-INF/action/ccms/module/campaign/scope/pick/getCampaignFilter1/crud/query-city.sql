select
domain_text_cn as city_name 
,domain_value as city_code
from
t_domain
where
namespace='City'
and
is_enabled='1'