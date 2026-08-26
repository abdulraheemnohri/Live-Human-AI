#ifndef LIVEHUMANAI_DIAGNOSTICS_H
#define LIVEHUMANAI_DIAGNOSTICS_H

#include <string>
#include <vector>

namespace livehumanai {
namespace diagnostics {

struct DiagnosticResult {
    std::string name;
    bool passed;
    std::string message;
};

class Diagnostics {
public:
    static std::vector<DiagnosticResult> runFullDiagnostic();
};

} // namespace diagnostics
} // namespace livehumanai

#endif
