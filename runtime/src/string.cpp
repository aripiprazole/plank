#include "plank/object.h"

extern "C" {
const char *string_string(plank::Object *o) {
  return o->toString(o);
}
}
