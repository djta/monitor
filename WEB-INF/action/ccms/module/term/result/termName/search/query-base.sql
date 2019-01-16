select term_name  
from t_term 
where 
tenantry_id=${def:tenantry}
${filter}
${orderby}