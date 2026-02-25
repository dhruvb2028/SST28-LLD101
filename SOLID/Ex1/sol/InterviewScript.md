# Interview Script: Explaining the SRP Refactor for Student Onboarding (Ex1)

"Thank you for the opportunity. I'd like to explain how I refactored the Student Onboarding Registration system to follow the Single Responsibility Principle (SRP).

Initially, the `OnboardingService` class was doing too much: it parsed input, validated data, generated IDs, saved to the database, and printed output—all in one method. This made the code hard to maintain, test, and extend.

To address this, I broke down the responsibilities into dedicated classes:

1. **StudentInputParser**: This class takes the raw input string and parses it into a structured `StudentInput` object. This keeps parsing logic isolated and reusable.

2. **StudentValidator**: This class checks the parsed input for errors, such as missing fields or invalid formats. It returns a list of error messages, making validation logic clear and testable.

3. **IdGenerator**: This class is responsible for generating unique student IDs. By isolating this logic, we can easily change the ID format in the future if needed.

4. **StudentDb (interface) & FakeDb (implementation)**: I introduced a `StudentDb` interface to decouple the onboarding flow from the actual database implementation. `FakeDb` implements this interface for in-memory storage, but we could swap in a real database later without changing the onboarding logic.

5. **StudentPrinter**: This class handles all printing and formatting of output, such as confirmation messages and errors. This keeps presentation logic separate from business logic.

Now, the `OnboardingService` class simply coordinates these components. It parses the input, validates it, generates an ID, saves the record, and prints the result—each step delegated to the appropriate class.

This refactor makes the code much easier to test, maintain, and extend. For example, if requirements change for validation or output formatting, we only need to update one class. It also demonstrates a clear application of the Single Responsibility Principle, which is a key part of SOLID design.

In summary, by separating concerns and delegating responsibilities, the code is now modular, testable, and ready for future changes."
