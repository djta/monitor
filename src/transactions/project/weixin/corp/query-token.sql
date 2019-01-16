select
t1.access_token,
t1.token_created as addtime,
t1.token_expires as expires_ib
from
wx_company t1
where
t1.tuid=${fld:corp_tuid}
