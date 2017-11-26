DELETE FROM
    `sys_upload_files`
WHERE
    `update-time` < CURDATE();