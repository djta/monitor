select
	corp_id
	,secret
	,access_token
	,token_created
	,token_expires
from
	wx_company
where
	tuid = '${tuid}'