select app_alias, description, pwd_policy from ${schema}s_application
where app_id = ${fld:webapp}
