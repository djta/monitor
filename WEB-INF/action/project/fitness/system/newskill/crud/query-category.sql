SELECT 
	domain_value, domain_text_cn 
FROM t_domain 
WHERE namespace = 'staffCategory' AND is_enabled = '1'
