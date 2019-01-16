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
    ,salary
    ,remark
    ,email
    ,mobile
    ,tenantry_id
    ,def_subject_id
    ,org_id
    ,parent_user
    ,user_pinyin
    ,remind_type
    ,ext_num
)
VALUES
(
      ${seq:currval@${schema}seq_user}
    , ${fld:name}
    , ${fld:sex}
    , ${fld:birthday}
    , ${fld:address}
    , ${fld:userlogin}
    , ${fld:contace_info}
    , ${fld:card_no}
    , ${fld:entry_date}
    , ${fld:contract_from}
    , ${fld:contract_end}
    , ${fld:salary}
    , ${fld:remark}
    , ${fld:email}
    , ${fld:mobile}
    , ${def:tenantry}
    ,(select subject_id from t_subject_tenantry t where t.is_default='1' and t.tenantry_id= ${def:tenantry})
    , ${fld:org_id}
    , ${fld:vipuserlogin}
    , ${fld:user_pinyin}
    , ${fld:remind_type}
    , ${fld:ext_num}
)