update hr_staff set 
  name = ${fld:name},
  name_en = ${fld:name_en},
  mobile = ${fld:mobile},
  salary = ${fld:salary},
  data_limit = ${fld:data_limit},
  contact = ${fld:vc_contact},
  remark = ${fld:remark},
  entry_date = ${fld:entry_date},
  
 email= ${fld:email},
address= ${fld:address},
sex= ${fld:sex},
user_pinyin= ${fld:user_pinyin},
birth= ${fld:birth},
wx= ${fld:wx},
card_no= ${fld:card_no},
school= ${fld:school},

origin= ${fld:origin},
bankcard= ${fld:bankcard},
education= ${fld:education},
otherperson= ${fld:otherperson},
bankname= ${fld:bankname},
major= ${fld:major}
  
where user_id = ${fld:user_id}
