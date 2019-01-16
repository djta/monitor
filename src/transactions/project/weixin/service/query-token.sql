select
t1.access_token,
t1.token_created as addtime,
t1.token_expires as expires_ib,
appid,
appsecret
from
wx_service t1
where
t1.tuid='${service_id}'
