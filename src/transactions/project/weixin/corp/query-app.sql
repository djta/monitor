select t2.tuid,t1.callback_token,t1.encoding_aeskey,t2.secret,t1.welcome_message,t1.app_name
 from wx_company_app t1 join wx_company t2 
on t1.company_id=t2.tuid
where
t1.tuid='${tuid}'


