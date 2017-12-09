select
  count(1)
from
  sys_userinfo
where
  userid = ? or tel = ? or email = ?