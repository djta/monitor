select 
dealer_code as dealer_code,
dealer_name_${def:locale} as dealer_name 
from cc_dealer 
where subject_id=${def:subject}
order by dealer_name_${def:locale};