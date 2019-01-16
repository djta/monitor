delete from
${schema}s_session
where
userlogin='${def:user}'
and
remote_addr = '${def:remoteaddr}'