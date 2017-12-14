//
// 修正 beetl 语法与 js 语法不兼容
//

//
// fix`^\s*for \(var (\S+) in roleList\) \{ var role = roleList\[\1\]`
//
var dt=sys.currentTimeString();
var roleList = sys.split(roleid, ",");
for (role in roleList) {
  var sql_role="select roleid from sys_dept_role where deptid=? and roleid=?";
  var param_role=[deptid, role];
  if (sql.query(sql_role,param_role) == 0) {
    var paramInsert = [deptid,role,"1",dt,dt];
    sql.update(sqlInsert,paramInsert,"1");
  }
//
// fix`^\s*for \(var (\S+) in sys\.result\[\"leaf\"\]\) \{ var row = sys\.result\[\"leaf\"\]\[\1\]`
//
  for(row in sys.result["leaf"]){
    list.add(rParams,[roleid,row["menuid"],"1",dt,dt]);
  }
}


//
// 注释不应该处理
// fix`\/\* for \(row in a\) \*\/`
//
/* for (row in a) */


sql=sql+" order by a.id desc";
sql.queryPaging(sql,params,pagenum,pagesize,"data");
//
// fix`^\s*for \(var (\S+) in sys\.result\.data\) \{ var r = sys\.result\.data\[\1\]`
//
for(var r in sys.result.data){ // 修正
    var op_detail="";
    var before_json=sys.instanceFromJson(r.before_json);
    var after_json=sys.instanceFromJson(r.after_json);
    //00101 元数据注册
    if(r.operation_type=="00101"){
        op_detail="新增元数据类："+r.typecd+"-"+r.typenm;
    }
}

//
// fix`^jexfor\(a in t\) \{`
// 不应该替换
//
jexfor(a in t) {
}

//
// 字符串不处理
// fix`^\'\\\'for \(role in roleList\) \{\\\'\'`
//
'\'for (role in roleList) {\''


//
// 修正 @list.add(...)
//
// fix`^__inner_call\(\"add\", params, status\)`
// fix`^__inner_call\(\"add\", param, apiNm\)`
//
@params.add(status)
@param.add(apiNm);

//
// 字符串不处理
// fix`^\'\\\'@param\.add\(about\);\'`
// fix`^\"\\\"@param\.add\(about\);\"`
//
'\'@param.add(about);'
"\"@param.add(about);"

//
// 不应该处理
//
// fix`^x@f.e\(xxx\);`
//
x@f.e(xxx);
}

//
// fix`^__inner_call\(\"add\", param, appid\)`
// fix`^__inner_call\(\"add\", param, moduleid, abc\)`
//
@param.add(appid); // 修正
@param.add(moduleid, abc); // 修正
if (status != null) {
  sql = sql + " where sys_apis.status = ? ";
  //
  // fix`^\s+__inner_call\(\"add\", param, status1\)`
  //
  @param.add(status1); // 修正
}

if (inner_flag != null) {
  sqlWhere = sqlWhere + " AND a.inner_flag = ?";
  //
  // fix`^\s+__inner_call\(\"add\", paramSel, inner_flag\)`
  //
  @paramSel.add(inner_flag); // 修正
}
if (status != null) {
  sqlWhere = sqlWhere + " AND a.status = ?";
  //
  // fix`^\s+__inner_call\(\"add\", paramSel, status\)`
  //
  @paramSel.add(status); // 修正
}


//
// fix`__virtual_attr\(menuid_array, \"size\"\)`
// fix`__virtual_attr\(sorting_order_array, \"size\"\)`
//
if(menuid_array.~size==sorting_order_array.~size){
    var sql="update sys_menu set sorting_order=? where menuid=?";
    var params=[];
    var i=0;
    while(i<menuid_array.~size){
    }
}
//
// fix`__virtual_attr\(id_array, \"size\"\)`
//
while(i<id_array.~size){
}
//
// fix`__virtual_attr\(id_array1, \"size\"\)`
//
while(j<id_array1.~size){}
//
// fix`__virtual_attr\(apptemp, \"size\"\)`
//
if(apptemp.~size==0){}
//
// fix`__virtual_attr\(treeMod, \"size\"\)`
//
if(treeMod.~size==0){}

// fix`dontmodify\.~size`
// dontmodify.~size
/* dontmodify.~size */