insert into ws_account_list
(
account_list_id
,account_id
,userlogin
,goods_id
,amount_fee
,amount_category
,account_balance
,state
,created
,createdby
,updated
,updatedby
,snapshot
)
select 
${seq:nextval@seq_ws_account_list}
,a.account_id
,'${def:user}'
,null
,${change_money}
,'1'
,a.account_balance
,'1'
,{ts '${def:timestamp}'}
,'${def:user}'
,null
,null
,0
from hr_staff s inner join ws_account a on s.userlogin=a.userlogin where s.userlogin='${def:user}'