# AGENTS.md

## Scope
These instructions apply to the entire repository rooted at this directory.

## Project Collaboration Rules

### 1. Work from screenshots and incremental progress
- The user may provide screenshots of documents, pages, errors, or console output.
- Use those screenshots as the current source of truth and improve the project step by step based on them.
- Continue iterating until the user indicates that the current project goal is complete.

### 2. Do not expand scope without confirmation
- If you identify a possible extension, optimization, or extra feature beyond the user's current request, do not implement it immediately.
- First explain the extension briefly and ask for confirmation.
- Only implement the extension after the user explicitly agrees.

### 3. Maintain development documentation
- When the user asks to record or summarize progress, append the new work to the specified Markdown document instead of rewriting unrelated sections.
- Prefer preserving the user's existing writing structure.
- When adding summaries, include both:
  - technical details
  - plain-language explanations that are easy to understand

### 4. Interview-style explanation rules
- Assume the user wants content that can be used in interviews when discussing completed modules.
- For each major module or feature, explain it from the perspective of a technical interviewer.
- Cover at least these angles when relevant:
  - how the module was designed
  - how the module was implemented
  - how the module was tested
  - common interview theory and underlying principles related to the module
- Do not answer these topics in a vague or high-level-only way.
- The explanation must include concrete technical details from the actual project.
- After the technical explanation, also provide a simpler "plain language" explanation.

### 5. Testing expectations
- After making code changes, verify them whenever feasible.
- Prefer targeted verification first, then broader checks if needed.
- If a full end-to-end verification cannot be completed because of environment or network constraints, state that clearly and still run the most meaningful local validation available.

### 6. Communication style for this repository
- Be concise, but do not skip important technical detail.
- When explaining architecture or debugging, connect the explanation back to the actual files and behavior in this project.
- When the user asks "what is this" or "what does this log mean", explain it in a way that helps them use it for debugging and for interviews.

## Reusable Trigger Phrase
- If the user says phrases such as:
  - "执行我的项目文档规则"
  - "按我的项目规则来"
  - "按 AGENTS 规则执行"
  then follow all rules in this file for the current task.
