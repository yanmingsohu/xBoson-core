SELECT
    `apiPath`,
    `offline_gpk`,
    `create_userid`,
    `physical_channel`,
    `physical_chain`
FROM
    `sys_chain`
Where
     status = '1'
     And chain_id = ?
