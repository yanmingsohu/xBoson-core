//
// 修正 beetl 语法与 js 语法不兼容
//
for (role in roleList) { // 修正
  var paramDelete = [pid,role];
  sql.update(sqlDelete,paramDelete,"1");
}

var dt=sys.currentTimeString();
var roleList = sys.split(roleid, ",");
for (role in roleList) { // 修正
  var sql_role="select roleid from sys_dept_role where deptid=? and roleid=?";
  var param_role=[deptid, role];
  if (sql.query(sql_role,param_role) == 0) {
    var paramInsert = [deptid,role,"1",dt,dt];
    sql.update(sqlInsert,paramInsert,"1");
  }

  for(row in sys.result["leaf"]){ // 修正
    list.add(rParams,[roleid,row["menuid"],"1",dt,dt]);
  }
}



sql=sql+" order by a.id desc";
sql.queryPaging(sql,params,pagenum,pagesize,"data");
//操作详细
for(r in sys.result.data){ // 修正
    var op_detail="";
    var before_json=sys.instanceFromJson(r.before_json);
    var after_json=sys.instanceFromJson(r.after_json);
    //00101 元数据注册
    if(r.operation_type=="00101"){
        op_detail="新增元数据类："+r.typecd+"-"+r.typenm;
    }
}

// 不应该替换
jexfor(a in t) {
}

//
// 字符串不处理
//
'\'for (role in roleList) {\''


//
// 修正 @list.add(...)
//
@params.add(status) // 修正
@param.add(apiNm); // 修正

//
// 字符串不处理
//
'\'@param.add(about);'
"\"@param.add(about);"
//
// 不应该处理
//
x@f.e(xxx);

}
@param.add(appid); // 修正
@param.add(moduleid); // 修正
if (status != null) {
  sql = sql + " where sys_apis.status = ? ";
  @param.add(status); // 修正
}

if (inner_flag != null) {
  sqlWhere = sqlWhere + " AND a.inner_flag = ?";
  @paramSel.add(inner_flag); // 修正
}
if (status != null) {
  sqlWhere = sqlWhere + " AND a.status = ?";
  @paramSel.add(status); // 修正
}

