INSERT INTO ws_account_list(
	account_list_id,
	account_id,
	userlogin,
	goods_id,
	amount_fee,
	amount_category,
	account_balance,
	state,
	created,
	createdby,
	recharge_type,	/** 充值类型(0-实充，1-赠送，2提成)*/
	remark
) VALUES (
	nextval('seq_ws_account_list'), 
	${fld:account_id}, 
	${fld:userlogin}, 
	${fld:goods_id}, 
	'${fee}', 
	'10', 
	(SELECT account_balance FROM ws_account WHERE account_id = ${fld:account_id}), 
	'1', 
	{ts '${def:timestamp}'},
	'${def:user}',
	'${recharge_type}', 
	'${remark}'
);