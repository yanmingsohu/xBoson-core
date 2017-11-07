var assert = module.exports = ok;

assert.ok = ok;
assert.eq = equal;
assert.equal = equal;
assert.deepEqual = deepEqual;
assert.deepStrictEqual = deepStrictEqual;
assert.doesNotThrow = doesNotThrow;

assert.AssertionError = AssertionError;


function ok(value, message) {
  if (!value)
    throw new AssertionError(message);
}


function equal(a, b, message) {
  if (a !== b)
    throw new AssertionError(message, a, b, "!=");
}


function deepEqual(actual, expected, message) {
  if (actual == expected)
    return;

  var a = 0;
  for (var n in actual) {
    deepEqual(actual[n], expected[n], message);
    ++a;
  }

  var b = 0;
  for (var n in expected) {
    deepEqual(actual[n], expected[n], message);
    ++b;
  }

  if (a == 0 && b == 0) {
    throw new AssertionError(message, actual, expected, "!=");
  }
}


function deepStrictEqual(actual, expected, message) {
  if (actual === expected)
      return;

  var a = 0;
  for (var n in actual) {
    deepStrictEqual(actual[n], expected[n], message);
    ++a;
  }

  var b = 0;
  for (var n in expected) {
    deepStrictEqual(actual[n], expected[n], message);
    ++b;
  }

  if (a == 0 && b == 0) {
    throw new AssertionError(message, actual, expected, "!==");
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



function AssertionError(msg, actual, expected, op) {
  this.name     = 'AssertionError';
  this.actual   = actual;
  this.expected = expected;
  this.op       = op || '!=';
  this.message  = (msg || 'Fail');

  if (actual || expected) {
    this.message = this.message
        + " actual " + (op||'!=') + " expected"
        + '\n\t\t  actual: ' + JSON.stringify(actual)
        + '\n\t\texpected: ' + JSON.stringify(expected);
  }
}
AssertionError.prototype = new Error();