module.exports = {
  terr : terr,
  t2   : t2,
  t3   : t3,
  getGlobalA : getGlobalA,
};


function terr(i, msg) {
  if (--i <= 0) {
    throw new Error(msg);
  } else {
    terr(i, msg);
  }
}


function t2() {
  return crossval;
}


function t3(a) {
  if (global.a != a)
    throw new Error("bad global.a " + a);
}


function getGlobalA() {
  return global.a;
}