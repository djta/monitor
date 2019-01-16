select
	ifexists_column('${table}','${field_code}') as flag
from 
	dual
