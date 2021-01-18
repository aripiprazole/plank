#ifndef PLANK_RUNTIME_INCLUDE_PLANK_OBJECT_H_
#define PLANK_RUNTIME_INCLUDE_PLANK_OBJECT_H_

namespace plank {

struct Object {
  const char *type{};
  int size{};
  void *value{};

  const char *(*toString)(plank::Object *){};

  Object(const char* type, void* value);
};

}

extern "C" {

plank::Object *Plank_Create_Object(const char *type, void *value);

plank::Object *Plank_Create_String(char *s);

plank::Object *Plank_Create_Double(double *i);

plank::Object *Plank_Create_Num(int *i);

plank::Object *Plank_Create_Bool(bool *b);

}

#endif //PLANK_RUNTIME_INCLUDE_PLANK_OBJECT_H_