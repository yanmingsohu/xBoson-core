-- 现在只支持平台机构共享应用
SELECT distinct sra.appid
  FROM sys_pl_application_release a
  Left join sys_pl_role_release r
    on r.applicationid = a.applicationid
  Left join sys_role sr
    on r.local_roleid = sr.roleid
  Left join sys_role_api sra
    on sra.roleid = sr.roleid
