var assert = module.exports = ok;

assert.ok = ok;
assert.eq = equal;
assert.equal = equal;
assert.deepEqual = deepEqual;
assert.deepStrictEqual = deepStrictEqual;
assert.doesNotThrow = doesNotThrow;
assert.fail = fail;
assert.ifError = ifError;
assert.notDeepEqual = notDeepEqual;
assert.notDeepStrictEqual = notDeepStrictEqual;
assert.notEqual = notEqual;
assert.notStrictEqual = notStrictEqual;
assert.strictEqual = strictEqual;
assert.throws = throws;

assert.AssertionError = AssertionError;

Object.freeze(assert);


function ok(value, message) {
  if (!value)
    throw new AssertionError(message);
}


function equal(a, b, message) {
  if (a != b)
    throw new AssertionError(message, a, b, "!=");
}


function strictEqual() {
  if (actual !== expected) {
    throw new AssertionError(message, actual, expected, "!==");
  }
}


function _deep_loop(actual, expected, eqfn) {
  if (eqfn(actual, expected)) {
    return;
  }

  if (actual && expected && actual.constructor == Array
        && expected.constructor == Array) {

    if (actual.length == expected.length) {
      for (var i=0; i<actual.length; ++i) {
        if (actual[i] != expected[i]) {
          throw new AssertionError();
        }
      }
    }
  } else {
    var a = 0, b = 0;
    for (var n in actual) {
      _deep_loop(actual[n], expected[n], eqfn);
      ++a;
    }
    for (var n in expected) {
      _deep_loop(actual[n], expected[n], eqfn);
      ++b;
    }
    if (a != b || (a==0 && b==0)) {
      throw new AssertionError();
    }
  }
}


function deepEqual(actual, expected, message) {
  try {
    _deep_loop(actual, expected, function(a, b) {
      return a == b;
    });
  } catch(e) {
    throw new AssertionError(message, actual, expected, "!=");
  }
}


function deepStrictEqual(actual, expected, message) {
  try {
    _deep_loop(actual, expected, function(a, b) {
      return a === b;
    });
  } catch(e) {
    throw new AssertionError(message, actual, expected, "!=");
  }
}


function fail(actual, expected, message, operator, stackStartFunction) {
  var e = new AssertionError(message, actual, expected, operator);
  if (stackStartFunction) {
    // maybe not working.
    Error.captureStackTrace(e, stackStartFunction);
  }
  throw e;
}


function ifError(value) {
  if (value) {
    throw value;
  }
}


function notDeepEqual(actual, expected, message) {
  try {
    deepEqual(actual, expected, message);
  } catch(e) {
    return;
  }
  throw new AssertionError(message, actual, expected, "==");
}


function notDeepStrictEqual(actual, expected, message) {
  try {
    deepStrictEqual(actual, expected, message);
  } catch(e) {
    return;
  }
  throw new AssertionError(message, actual, expected, "===");
}


function notEqual(actual, expected, message) {
  if (actual == expected) {
    throw new AssertionError(message, actual, expected, "==");
  }
}


function notStrictEqual(actual, expected, message) {
  if (actual === expected) {
    throw new AssertionError(message, actual, expected, "===");
  }
}


function doesNotThrow(block, error, message) {
  try {
    block();
  } catch(e) {
    if (e.constructor == error) {
      throw new AssertionError((message && (message + ":"))||'' + e.message);
    } else {
      throw e;
    }
  }
}


function throws(block, error, message) {
  var test = _make_test(error);
  try {
    block();
  } catch(e) {
    if (test(e)) {
      return;
    }
    throw new AssertionError(message, error.toString(), e.toString());
  }
  throw new AssertionError(message, 'throws error', 'not throw');
}


function _make_test(obj) {
  var test;
  if (obj instanceof RegExp) {
    test = function(e) {
      return obj.test(e.toString());
    };
  } else if (typeof obj == "string") {
    test = function(e) {
      return e.toString().indexOf(obj) >= 0;
    };
  } else if (obj == null) {
    test = function(e) {
      console.warn(e);
      return true;
    }
  } else {
    test = function(e) {
      if (e.constructor === obj) {
        return true;
      } else {
        return obj(e) === true;
      }
    };
  }
  return test;
}


function AssertionError(msg, actual, expected, op) {
  this.name     = 'AssertionError';
  this.actual   = actual;
  this.expected = expected;
  this.operator = op || '!=';
  this.message  = (msg || 'Fail');

  if (actual || expected) {
    this.message = this.message
        + " actual " + (this.operator) + " expected"
        + '\n\t\t  actual: ' + JSON.stringify(actual)
        + '\n\t\texpected: ' + JSON.stringify(expected);
  }
}
AssertionError.prototype = new Error();