#ifndef PLANK_RUNTIME_SRC_IO_H_
#define PLANK_RUNTIME_SRC_IO_H_

#include "plank/object.h"

extern "C" {

void io_println(plank::Object *message);

void io_print(plank::Object *message);

}

#endif //PLANK_RUNTIME_SRC_IO_H_
