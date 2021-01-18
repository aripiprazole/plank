#include "plank/io.h"
#include "plank/object.h"

#include <iostream>

extern "C" {

void io_println(plank::Object *message) {
  std::cout << message->toString(message) << std::endl;
}

void io_print(plank::Object *message) {
  std::cout << message->toString(message);
}

}

