select
domain_text_cn as series_name 
,domain_value as  series_code
from
t_domain
where
namespace='IntenedeSeries'
and
is_enabled='1'