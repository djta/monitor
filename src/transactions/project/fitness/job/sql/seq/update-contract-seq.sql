BEGIN TRANSACTION;

DROP SEQUENCE seq_cc_contract;
CREATE SEQUENCE seq_cc_contract INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
ALTER TABLE seq_cc_contract OWNER TO postgres;

END TRANSACTION;