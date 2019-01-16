select 
	user_id,
    userlogin,
    name,
    name_en,
    entry_date,
    mobile,
    contact,
   (
	select k.skill_id
	from hr_staff_skill fk 
	inner join hr_skill k on fk.skill_id = k.skill_id 
	where fk.user_id = f.user_id
    ) as skill_id,
      (case when status =1 then '在职' else '离职' end ) as  i_status,
    (
    	CASE WHEN salary IS NOT NULL THEN salary ELSE (
    		SELECT w.base_pay FROM hr_wage w 
    		INNER JOIN hr_staff_skill sk ON w.skill_id = sk.skill_id 
    		WHERE sk.user_id = f.user_id AND w.is_deleted = 0 
    		AND w.enabled = 0 LIMIT 1
    	) END
    ) AS salary,
    data_limit,
    replace(remark,'''','') as remark,
    
email,--email
address,--地址
sex,--性别
user_pinyin,--工号
birth,--生日
wx,
card_no,--身份证号
school,
(select isclass from hr_staff_skill where userlogin = f.userlogin) as isclass,

origin,--籍贯
bankcard,--银行卡号
education,--学历
otherperson,--其他联系人
bankname,--银行名字
major,--专业
created,
(select t.tuid from t_attachment_files t where t.pk_value = f.user_id::varchar and t.table_code = 'hr_staff' and t.org_id= ${def:org} order by t.tuid desc limit 1) as imgid,
 (case when status =1 then null else  updated end) as   updated
from hr_staff f 
where 
	f.user_id = ${fld:id}