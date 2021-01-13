#include "plank/IO.h"

#include <cstring>
#include <iostream>

extern "C" {
void io_println(char *message) {
  std::cout << message << std::endl;
}

void io_print(char *message) {
  std::cout << message;
}

char* io_toString(int i) {
  return strdup(std::move(std::to_string(i)).c_str());
}
}

