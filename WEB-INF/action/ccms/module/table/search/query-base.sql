SELECT
    t.tuid
    , t.table_alias
    , lower(t.table_code) as	table_code
    , lower(t.schema_name)	as	schema_name
    , t.table_name
    , t.created
    , t.remark
    , t.subject_id
  , case when (select count(*) from t_field f where f.table_id=t.tuid)=0 then '' else 'none' end     as  created_flag	/*如果表没有字段,则调用生成*/
  , case when (select count(*) from t_field f where f.table_id=t.tuid)=0 then 'none' else '' end     as  import_flag	/*如果表没有字段*/
  , case when (select count(*) from t_field f where f.table_id=t.tuid)=0 then 'none' else '' end     as  migrate_flag	/*数据迁移*/
FROM
	t_table t
WHERE
    t.deleted = '0'

${filter}

order by 
    t.tuid desc
