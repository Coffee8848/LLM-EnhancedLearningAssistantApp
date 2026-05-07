# LLM-Enhanced Learning Assistant App

A simple Android learning app built for SIT305 Task 6.1D.

## Features
- Login, register, and interest setup screens
- Home screen with generated task card
- Quiz screen with AI hints for Q1 and Q2
- Results screen with AI answer explanations
- AI-generated 3 flashcards from a topic
- AI-generated 7-day study plan based on quiz history
- Prompt and response shown in UI (for assignment requirement)
- Loading and error handling for LLM requests

## Tech Stack
- Android (Java + XML)
- SharedPreferences (local session/history)
- OkHttp (API calls)
- OpenAI Chat Completions API

## Project Structure (Key Files)
- `MainActivity.java` - login flow
- `RegisterActivity.java` - account setup
- `InterestsActivity.java` - topic selection
- `HomeActivity.java` - flashcards + study plan
- `QuizActivity.java` - questions + hints
- `ResultsActivity.java` - score + explanations
- `SessionManager.java` - local storage
- `LlmApiClient.java` - LLM API integration
- `DummyData.java` - hardcoded demo data


## Notes
- This app uses dummy data for learning content.
- API key is not included in this repository.
