var if_throw_ok = require("./util.js").if_throw_ok;
var assert = require("assert");
var Event = require("events");
var event = new Event();


var tt = 0;
event.on("a", function() {
  ++tt;
  console.debug("event working");
});
event.emit('a');
event.emit('a');
assert.eq(tt, 2, 'not recv event');

if_throw_ok(function() {
  event.emit("error", 'throws');
}, 'error event');

