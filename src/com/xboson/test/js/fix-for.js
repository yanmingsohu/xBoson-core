//
// 修正 beetl 语法与 js 语法不兼容
//
for (role in roleList) {
  var paramDelete = [pid,role];
  sql.update(sqlDelete,paramDelete,"1");
}


var dt=sys.currentTimeString();
var roleList = sys.split(roleid, ",");
for (role in roleList) {
  var sql_role="select roleid from sys_dept_role where deptid=? and roleid=?";
  var param_role=[deptid, role];
  if (sql.query(sql_role,param_role) == 0) {
    var paramInsert = [deptid,role,"1",dt,dt];
    sql.update(sqlInsert,paramInsert,"1");
  }

  for(row in sys.result["leaf"]){
    list.add(rParams,[roleid,row["menuid"],"1",dt,dt]);
  }
}