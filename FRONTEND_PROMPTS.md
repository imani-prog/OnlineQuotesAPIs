# Frontend Prompt Guide (Quotes + Groq)

Use this guide to shape frontend prompts and request payloads for the quote explanation and chat features.

## 1) Quote Explanation Prompting (backend already enforces this)

The backend already sends a system prompt that requires:
- clear, simple language
- 3-5 sentences
- one real-life example
- one lesson or moral

### Example UI helper text

Use this as a frontend hint near the "Explain" button:

"Get a short explanation with a real-life example and a lesson or moral."

### API request

```
POST /api/quotes/explain
Content-Type: application/json

{
  "text": "Things do not have meaning. We assign meaning to everything.",
  "author": "Tony Robbins"
}
```

### API response

```
{
  "text": "Things do not have meaning. We assign meaning to everything.",
  "author": "Tony Robbins",
  "explanation": "..."
}
```

## 2) Quote Chat Prompting (frontend controls the question)

The frontend should ask a focused follow-up question about the quote. Good question patterns:
- "How can I apply this in my daily routine?"
- "What is a real-life example of this quote?"
- "How does this help with decision-making?"
- "What lesson should I take from this?"

### API request

```
POST /api/quotes/chat
Content-Type: application/json

{
  "text": "Things do not have meaning. We assign meaning to everything.",
  "author": "Tony Robbins",
  "question": "How can I apply this when I feel stuck at work?",
  "history": [
    { "role": "user", "content": "Can you simplify the quote?" },
    { "role": "assistant", "content": "It means we interpret events and can change that interpretation." }
  ]
}
```

### API response

```
{
  "text": "Things do not have meaning. We assign meaning to everything.",
  "author": "Tony Robbins",
  "question": "How can I apply this when I feel stuck at work?",
  "answer": "..."
}
```

## 3) Frontend UX Tips

- Always show the quote text/author above the explanation or chat.
- When sending chat, include the last 3-5 messages in `history` for continuity.
- Keep user questions short and specific for better answers.
- Disable the chat send button while the API request is in flight.

## 4) Safety and Reliability

- If the response is empty, show: "No response from the AI. Please try again."
- If the API returns 503, show: "AI service is unavailable. Please try again shortly."


