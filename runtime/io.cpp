#include <iostream>

extern "C" {

void PLANK_INTERNAL_println(char *message) {
  std::cout << message << std::endl;
}

void PLANK_INTERNAL_print(char *message) {
  std::cout << message;
}

}

