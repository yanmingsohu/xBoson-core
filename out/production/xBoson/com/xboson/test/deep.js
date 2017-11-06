module.exports = {
  terr : terr,
};


function terr(i, msg) {
  if (--i <= 0) {
    throw new Error(msg);
  } else {
    terr(i, msg);
  }
}