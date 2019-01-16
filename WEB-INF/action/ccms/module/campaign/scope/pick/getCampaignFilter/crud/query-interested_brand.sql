select
domain_text_cn as brand_name 
,domain_value as  brand_code
from
t_domain
where
namespace='IntendedBrand'
and
is_enabled='1'