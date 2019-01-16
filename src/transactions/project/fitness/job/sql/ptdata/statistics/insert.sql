insert into cc_pt_data_statistics(
	tuid,
	results_money,
	task_money,
	completion,
	market_class_num,
	pt_inventory_num,
	pt_teaching_num,
	site_target_num,
	site_target_deal_num,
	site_target_deal_rate,
	pt_expire_num,
	pt_renewal_num,
	pt_renewal_rate,
	year,
	month,
	userlogin,
	org_id
)
values
(
	${seq:nextval@seq_cc_pt_data_statistics},
	${results_money},
	${task_money},
	${completion},
	${market_class_num},
	${pt_inventory_num},
	${pt_teaching_num},
	${site_target_num},
	${site_target_deal_num},
	${site_target_deal_rate},
	${pt_expire_num},
	${pt_renewal_num},
	${pt_renewal_rate},
	to_char('${def:date}'::date - interval '1 month','yyyy'),
	to_char('${def:date}'::date - interval '1 month','MM'),
	${fld:userlogin},
	${fld:org_id}
)

