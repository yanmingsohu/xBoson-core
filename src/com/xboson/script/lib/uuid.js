var sysuuid = require("sys/uuid");

var uuid = module.exports = {
};

[ 'v1', 'v4', 'v1obj', 'v4obj', 'ds', 'parseDS',
  'zip', 'unzip', 'getBytes',
].forEach(function(name) {
  uuid[name] = warp(name);
})

Object.freeze(uuid);


function warp(fn) {
  return function(a, b) {
    switch (arguments.length) {
      case 0:
        return sysuuid[fn]();
      case 1:
        return sysuuid[fn](a);
      case 2:
        return sysuuid[fn](a, b);
    }
  }
}