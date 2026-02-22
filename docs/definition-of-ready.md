# Definition of Ready — Quiz Pszczelarski (KMP)

> **Location:** `/docs/definition-of-ready.md`

---

## Purpose

Every feature or task entering development must meet these criteria.  
If any item is missing, the task is **not ready** and must be refined first.

---

## Checklist

- [ ] **User goal** clearly stated (what the user achieves)
- [ ] **Non-goals** listed (what is explicitly out of scope)
- [ ] **Platforms** impacted: Android / iOS / both
- [ ] **UX reference** provided (Figma link/frame) — if UI work
- [ ] **Data sources** identified (local JSON, SQLDelight, Firebase, API) and constraints noted
- [ ] **Acceptance criteria** — specific, testable conditions
- [ ] **Edge cases** listed (offline, empty state, error state, large data)
- [ ] **Offline behavior** defined (if applicable)
- [ ] **Telemetry / analytics** requirement stated (if any)
- [ ] **Test strategy** notes (what to test, what to mock)
- [ ] **Architecture impact** assessed (new module? new dependency? → ADR required)

---

## When to Skip

For trivial tasks (typo fix, config change), a subset is acceptable.  
For any task touching architecture, data flow, or new screens — full checklist applies.
