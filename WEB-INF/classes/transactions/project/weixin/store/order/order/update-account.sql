update ws_account
set account_balance = account_balance-${change_money}
,updated = {ts '${def:timestamp}'}
,updatedby = 'system'
from hr_staff s
where
s.userlogin = '${def:user}'
and s.userlogin = ws_account.userlogin