select
access_token,
t1.token_created,
t1.token_expires,
appid,
appsecret as secret
from
wx_service t1
where
t1.tuid='${service_id}'
