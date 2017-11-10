module.exports = {
  if_throw_ok : if_throw_ok,
};



function if_throw_ok(fn, emsg, showstack) {
  var _throw = 0;
  try {
    fn();
  } catch(e) {
    _throw = e;
    emsg += ' - [ ' + e + ' ]';
  }

  if (!_throw) {
    throw new Error(emsg);
  } else {
    if (showstack) {
      var arr = ["OK", emsg || fn.name];
      arr.push(_throw.stack);
      console.info(arr)
    }
  }
}