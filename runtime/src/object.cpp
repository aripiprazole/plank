#include <string>
#include <iostream>

#include "plank/object.h"

plank::Object::Object(const char *type, void *value) : type(type), value(value) {
  size = -1;
  toString = [](plank::Object *o) {
    std::string ptr = std::to_string(reinterpret_cast<unsigned long>(o->value));
    std::string str = std::string(o->type) + "@" + ptr;
    auto *dump = new std::string(str);

    return dump->c_str();
  };
}

extern "C" {

plank::Object *Plank_Create_Object(const char *type_, void *value) {
  std::string type(type_);

  if (type == "*Char") {
    return Plank_Create_String(reinterpret_cast<char *>(value));
  } else if (type == "Double") {
    return Plank_Create_Double(reinterpret_cast<double *>(value));
  } else if (type == "Int") {
    return Plank_Create_Num(reinterpret_cast<int *>(value));
  } else if (type == "Bool") {
    return Plank_Create_Bool(reinterpret_cast<bool *>(value));
  } else {
    return new plank::Object(type.c_str(), value);
  }
}

plank::Object *Plank_Create_String(char *s) {
  auto *o = new plank::Object("String", s);

  o->toString = [](plank::Object *o) {
    return reinterpret_cast<const char *>(o->value);
  };

  return o;
}

plank::Object *Plank_Create_Double(double *i) {
  auto *o = new plank::Object("Double", i);

  o->toString = [](plank::Object *o) {
    auto *v = (double *) o->value;

    return (new std::string(std::to_string(*v)))->c_str();
  };

  return o;
}

plank::Object *Plank_Create_Num(int *i) {
  auto *o = new plank::Object("Int", i);

  o->toString = [](plank::Object *o) {
    auto *v = (int *) o->value;

    return (new std::string(std::to_string(*v)))->c_str();
  };

  return o;
}

plank::Object *Plank_Create_Bool(bool *b) {
  auto *o = new plank::Object("Bool", b);

  o->toString = [](plank::Object *o) {
    auto v = reinterpret_cast<unsigned long>(o->value);
    if (v == 1) {
      return "true";
    } else if (v == 0) {
      return "false";
    } else {
      return (new std::string(std::to_string(v)))->c_str();
    }
  };

  return o;
}

}
