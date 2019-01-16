update et_account 
set 
	signature = '${signature}'
	,expire_time = to_timestamp(${expire_time}) 
where tuid = ${fld:tuid}
