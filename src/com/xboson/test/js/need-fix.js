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



sql=sql+" order by a.id desc";
sql.queryPaging(sql,params,pagenum,pagesize,"data");
//操作详细
for(r in sys.result.data){
    var op_detail="";
    var before_json=sys.instanceFromJson(r.before_json);
    var after_json=sys.instanceFromJson(r.after_json);
    //00101 元数据注册
    if(r.operation_type=="00101"){
        op_detail="新增元数据类："+r.typecd+"-"+r.typenm;
    }
}


//
// 修正 @list.add(...)
//
@params.add(status)