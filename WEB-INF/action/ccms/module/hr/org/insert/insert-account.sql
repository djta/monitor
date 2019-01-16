INSERT INTO ws_account(
	 account_id
	,account_name
	,account_balance
	,created
	,createdby
	,userlogin
	,account_type
) VALUES(
	 ${seq:nextval@seq_ws_account}
	,${fld:org_name}
	,0.00
	,{ts '${def:timestamp}'}
	,'${def:user}'
	,(${seq:currval@seq_hr_org})::varchar
	,2/** 账户类型：0会员，1服务提供者，2服务门店 */
)