insert into hr_staff
(
email,--email
address,--地址
sex,--性别
user_pinyin,--工号
birth,--生日
wx,
card_no,--身份证号
school,

origin,--籍贯
bankcard,--银行卡号
education,--学历
otherperson,--其他联系人
bankname,--银行名字
major,--专业

    user_id,
    userlogin,--登录名
    name,--姓名
    name_en,--别名
    mobile,--电话
    contact,--其他联系人
  --  salary,
    data_limit,--权限
    remark,--备注
    status,
    entry_date,
    org_id,
    created,
    createdby
)
values 
(
  ${fld:email},
 ${fld:address},
  ${fld:sex},
  ${fld:user_pinyin},
    ${fld:birth},
   ${fld:wx},
      ${fld:card_no},
   ${fld:school},
   
   ${fld:origin},
   ${fld:bankcard},
   ${fld:education},
   ${fld:otherperson},
   ${fld:bankname},
   ${fld:major},
   
  
	${seq:nextval@${schema}seq_user},
    ${fld:userlogin},
    ${fld:name},
    ${fld:name_en},
    ${fld:mobile},
    ${fld:vc_contact},
   -- ${fld:salary},
    ${fld:data_limit},
    ${fld:remark},
    1,
    ${fld:entry_date},
 	case when ${fld:org_id} is not null then ${fld:org_id} else ${def:org} end,
	{ts '${def:timestamp}'},
	'${def:user}'
)
