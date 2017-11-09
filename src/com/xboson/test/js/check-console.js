var assert = require("assert");
var c = require("console");

c.debug("module console.debug ok");
c.log("module console.log ok");
c.info("module console.info ok");
c.warn("module console.log ok");
c.error("module console.error ok");
c.fatal("module console.fatal ok");

//
// 使用默认 console 是最佳方案
//
console.debug("module console.debug ok");
console.log("module console.log ok");
console.info("module console.info ok");
console.warn("module console.log ok");
console.error("module console.error ok");
console.fatal("module console.fatal ok");