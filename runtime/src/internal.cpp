#include<string>
#include<cstring>

extern "C" {

char *internal_Plank_Internal_concat(char *lhs, char *rhs) {
  std::string concatenated = std::string(lhs) + std::string(rhs);

  return strdup(concatenated.c_str());
}

}
