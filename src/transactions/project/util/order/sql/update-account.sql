UPDATE ws_account 
SET 
	account_balance = ${balance}, 
	updated = {ts '${def:timestamp}'},
	updatedby = '${def:user}'
WHERE account_id = ${fld:account_id}
