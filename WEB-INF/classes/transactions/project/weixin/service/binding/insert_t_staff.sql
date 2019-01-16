INSERT INTO
	hr_staff
(
    user_id
    ,name
    ,userlogin
    ,remark
    ,mobile
    ,tenantry_id
    ,def_subject_id
    ,weixin_service_id
    ,staff_category
    ,weixin_lastlogin
)
VALUES
(
      ${seq:currval@${schema}seq_user}
    , ${fld:name}
    , ${fld:userlogin}
    , '微信绑定创建'
    , ${fld:mobile}
    , (select tenantry_id from wx_service t where t.wxid= ${fld:weixin_id})
    , (select subject_id from t_subject_tenantry t where t.is_default='1' and t.tenantry_id= (select tenantry_id from wx_service t where t.wxid= ${fld:weixin_id}))
	,(select tuid from wx_service t where t.wxid= ${fld:weixin_id})
	,'0' --会员
	,${fld:weixin_id}
    )