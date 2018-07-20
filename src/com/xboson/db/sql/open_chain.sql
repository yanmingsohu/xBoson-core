SELECT
    `offline_gpk`,
    `create_userid`,
    `physical_channel`,
    `physical_chain`
FROM
    `sys_pl_chain`
Where
     status = '1'
     And chain_id = ?
