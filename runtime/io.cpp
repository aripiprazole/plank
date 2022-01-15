#include <iostream>

extern "C" {

void anonymous_println(char *message) {
  std::cout << message << std::endl;
}

void anonymous_print(char *message) {
  std::cout << message;
}

}

