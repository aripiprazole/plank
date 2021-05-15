#include <iostream>

extern "C" {

void IO_println(char *message) {
  std::cout << message << std::endl;
}

void IO_print(char *message) {
  std::cout << message;
}

}

