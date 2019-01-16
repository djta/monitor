select
	app_id,
	app_alias,
	description
from ${schema}s_application
where 1=1

${filter}
${orderby}
