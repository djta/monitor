DECLARE

appid NATURAL;

BEGIN

SELECT ${schema}seq_application.NEXTVAL INTO appid FROM dual;

insert into ${schema}s_application
(app_id, app_alias, description, pwd_policy)
values
(appid,${fld:app_alias},${fld:description},${fld:pwd_policy});

