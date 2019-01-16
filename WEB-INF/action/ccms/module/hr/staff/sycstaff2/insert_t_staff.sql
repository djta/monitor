INSERT INTO
	hr_staff
(
    user_id
    ,name
    ,sex
    ,birthday
    ,address
    ,userlogin
    ,contace_info
    ,card_no
    ,entry_date
    ,contract_from
    ,contract_end
    ,hourly_rate
    ,remark
    ,email
    ,mobile
    ,tenantry_id
    ,def_subject_id
    ,org_id
    ,hc_id
    ,staff_category
    ,parent_user
    ,user_pinyin
)
VALUES
(
      ${fld:user_id}
    , ${fld:userlogin}
    , 0
    , null
    , null
    , ${fld:userlogin}
    , null
    , null
    , null
    , null
    ,null
    , null
    ,null
    , null
    , null
    , ${def:tenantry}
    , (select subject_id from t_subject_tenantry t where t.is_default='1' and t.tenantry_id= ${def:tenantry} limit 1)
    , null
    , null
    , null
    , null
    , null
)