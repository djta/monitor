INSERT INTO cc_captcha(
	tuid,
	mobile,
	captcha_code,
	created,
	continue_time,
	sms_id
) VALUES(
	${seq:nextval@seq_cc_captcha},
	'${mobile}',
	'${captcha_code}',
	'${created}',
	'${continue_time}',
	${seq:nextval@seq_cc_sms}
)