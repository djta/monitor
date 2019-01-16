select 
domain_value as province_value,
domain_text_${def:locale} as province_name 
from t_domain 
where namespace='Province' and is_enabled='1'
order by show_order,domain_text_${def:locale};



