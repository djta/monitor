select
  tuid,
  term_name
from t_term
where
  term_type='9'
ORDER BY term_type DESC
