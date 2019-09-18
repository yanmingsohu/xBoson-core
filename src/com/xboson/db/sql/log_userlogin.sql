UPDATE `sys_userinfo`
SET
  `last_ip` = ?,
  `last_dt` = now()
WHERE `pid` = ?;
