select
	'FAQ更新通知' as title,
	'${def:user}' as sender,
	'${def:timestamp}' as send_date
from 
	dual