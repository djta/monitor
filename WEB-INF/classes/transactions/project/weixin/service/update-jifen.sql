update
ws_account
set
integral=  (case when   ${jifen}<${integral}     then   ${integral} - jifen else 0 end ),
account_balance=(case when   ${use_jifen}<${account_balance}     then   ${account_balance} - use_jifen else 0 end ),
where 
userlogin='{def:user}'