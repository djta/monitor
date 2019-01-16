SELECT
      f.show_name
      ,f.lable
      ,f.content
      ,f.tuid as faq_file_id
      ,'' as  file_link
      ,case when f.is_expired='1' then '过期' else '未过期' end as is_expired
      ,f.create_date
FROM
      t_faq f
WHERE
      f.tuid = ${fld:faq_id}