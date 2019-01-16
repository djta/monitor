select 
	tuid,
	term_name,
	(case when term_type = '0' then '评价' when term_type = '1' then '问卷' when term_type='9' then '题库' else '未知' end) as term_type,
	remark,
	(case when status = '0' then '禁用' else '启用' end) as status,
    (select t2.term_name from t_term t2 where t2.tuid=t1.question_bank_id) as question_bank_name
 from 
 	t_term t1
 where
 	tenantry_id = ${def:tenantry}
 	${filter}
 	${orderby}