insert into cc_mc_data_statistics(
	tuid,
	results_money,
	task_money,
	strange_reception_num,
	strange_deal_num,
	appointment_visit_num,
	appointment_deal_num,
	cust_expire_num,
	cust_renewal_num,
	year,
	month,
	userlogin,
	org_id,
	completion,
	strange_transaction_rate,
	appointment_rate,
	mc_renewal_rate
)
values
(
	${seq:nextval@seq_cc_mc_data_statistics},
	${results_money},
	${task_money},
	${strange_reception_num},
	${strange_deal_num},
	${appointment_visit_num},
	${appointment_deal_num},
	${cust_expire_num},
	${cust_renewal_num},
	to_char('${def:date}'::date - interval '1 month','yyyy'),
	to_char('${def:date}'::date - interval '1 month','MM'),
	${fld:userlogin},
	${fld:org_id},
	${completion},
	${strange_transaction_rate},
	${appointment_rate},
	${mc_renewal_rate}
)

