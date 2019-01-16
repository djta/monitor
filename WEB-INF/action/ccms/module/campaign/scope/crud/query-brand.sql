select
domain_value as brand_value,
domain_text_${def:locale} as brand_name 
from t_domain 
where namespace='IntendedBrand' and is_enabled='1'
order by show_order,domain_text_${def:locale};