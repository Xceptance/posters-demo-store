# Test Automation Backlog

This file contains manual test cases that are currently failing due to known bugs. They should be converted to test automation once the underlying issues are resolved.

## Search Domain

- **TC_SRC_001**: Simple Search — Single Term (Happy Path)
  - *Status*: FAILED (DE "Hornisse" not found, JA currency mismatch)
- **TC_SRC_002**: Search — No Results
  - *Status*: FAILED (Empty state messages not localized for DE/SV/JA)
- **TC_SRC_004**: Multi-Word Search (AND Behavior)
  - *Status*: FAILED ("grizzly" alone fails, reversed terms fail, full desc paste fails)
- **TC_SRC_005**: Partial / Prefix Search
  - *Status*: FAILED ("griz" (US) and "モルフォ" (JP) prefix search fails)
- **TC_SRC_008**: Search Result Count & Heading
  - *Status*: FAILED (Heading not localized, stemmer false positives, "a" returns 95/124)
