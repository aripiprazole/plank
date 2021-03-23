#include "plank/io.h"
s
#include <iostream>

extern "C" {

void io_println(char *message) {
  std::cout << message << std::endl;
}

void io_print(char *message) {
  std::cout << message;
}

}

