var sysuuid = require("sys/uuid");
var sysutil = require("sys/util");

var uuid = module.exports = {
};

sysutil.warpObject(sysuuid, uuid);
