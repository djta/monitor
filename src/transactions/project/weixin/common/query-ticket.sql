select
jsapi_ticket,
t1.ticket_created as addtime,
t1.ticket_expires as expires_ib,
appid,
appsecret,
pay_sign_key,
t2.partner_key,
t2.partner_id
from wx_service t1 
left join wx_pay t2
on t1.pay_id=t2.tuid
where
t1.tuid='${tuid}'