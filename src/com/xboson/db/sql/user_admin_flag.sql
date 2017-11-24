select
       admin_flag
  from
       sys_tenant_user
 where
       pid = ?
   and orgid = ?