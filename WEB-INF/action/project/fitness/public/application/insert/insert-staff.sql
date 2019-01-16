INSERT INTO hr_staff(
    user_id
    ,name
    ,sex
    ,userlogin
    ,remark
    ,mobile
    ,org_id
) VALUES(
      ${user_id}
    , ${fld:name}
    , NULL
    , '${userlogin}'
    , NULL
    , ${fld:mobile}
    , (select min(org_id) from hr_org)
)