SELECT 
	a.account_id,
	a.userlogin,
	a.account_balance 
FROM ws_account a 
WHERE a.userlogin = '${userlogin}'
AND a.account_type = ${account_type}
