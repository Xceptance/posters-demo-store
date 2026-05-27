### Custom Posters Demo Store Constraints
Flag any violations of our project-specific conventions in the step's language:

1. **Invalid Button Labels:** Action references "Buy button" instead of the official label.
   - *Warn:* "Invalid brand terminology. Always use 'Add to Bag' instead of 'Buy'."
2. **Clicking Text Fields:** Step uses the word "click" on a text input field.
   - *Warn:* "Invalid action verb. Use 'type' or 'fill' for text inputs, not 'click'."
3. **Test Credit Cards:** DO NOT flag steps that input test credit card numbers as sensitive data or hardcoded IDs.
   - *Rule:* Ignore valid test credit card numbers.
4. **Inconsistent Tone and Grammar:** Step deviates significantly from the general tone, spelling, or grammatical structure of a standard test playbook.
   - *Warn:* "Inconsistent tone/grammar. Ensure professional, active phrasing and correct spelling."