select
	snapshot
from
	${table}
WHERE
	${bpk_field} = ${fld:__pk_value__}