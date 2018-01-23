
INSERT INTO `sys_pl_log_request`
  (
  `logid`,
  `log_time`,
  `log_level`,
  `log_error_type`,
  `requestid`,
  `serverid`,
  `log`,
  `orgid`,
  `pid`,
  `sysid`,
  `user_key`,
  `remote_ip`,
  `appid`,
  `moduleid`,
  `apiid`,
  `elapsed`,
  `user_referer`,
  `user_agent`,
  `cookie`,
  `createdt`
  )
VALUES
  (
  ?, -- logid
  ?, -- now(),
  'INFO',
  'NONE',
  ?, -- requestid
  null,
  ?, -- log
  ?, -- org
  ?, -- pid
  null,
  null,
  ?, -- <{remote_ip: }>,
  ?, -- <{appid: }>,
  ?, -- <{moduleid: }>,
  ?, -- <{apiid: }>,
  null,
  ?, -- <{user_referer: }>,
  ?, -- <{user_agent: }>,
  null, -- <{cookie: }>,
  now() -- <{createdt: }>
  );
