select
	title,
	position
from ${schema}s_menu
where app_id = ${fld:webapp}
