update t_faq
    set
     show_name         = ${fld:show_name}
    ,lable             = ${fld:lable}
    ,content           = ${fld:content}
    ,is_node           = ${fld:is_node}
    ,creator_id        = '${def:user}'
    ,start_date        = ${fld:start_date}
    ,end_date          = ${fld:end_date}
    ,remark            = ${fld:remark}
    ,time_stamp        = {ts '${def:timestamp}'}
    ,is_expired        = ${fld:is_expired}
    ,is_bulletin       = ${fld:is_bulletin}
where
    tuid               = ${fld:tuid}