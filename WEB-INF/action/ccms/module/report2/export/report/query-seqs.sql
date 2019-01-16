select
	${seq:nextval@${seq_name}} as seq_value
	,'${seq_name}' as seq_name
from 
	dual