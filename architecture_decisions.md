# Architecture Decisions & Trade-offs

## Purpose

This document explains the reasoning behind the architectural decisions made in this template.

The primary goal is to keep the project modular, maintainable, and adaptable. Each decision should solve a specific problem rather than introduce abstraction for its own sake.

---

## 1. Navigation as a Separate Core Module

**Decision:** Navigation is implemented in `core:navigation` rather than directly inside `shared` or individual features.

**Why?**

The main reason is to isolate the navigation library from the rest of the application.

Navigation is an infrastructure concern. Features should define their destinations and screens without depending heavily on the underlying navigation implementation.

By centralizing navigation-related code in one module:

- We can replace Navigation 3 with another navigation library without rewriting every feature.
- Changes to the navigation library's APIs are mostly contained within `core:navigation`.
- Features depend on our navigation contract rather than owning the application's back stack.
- Navigation behavior and back-stack ownership remain consistent across the application.

For example, if we decide to replace Navigation 3, the main changes should happen in `core:navigation`. The feature registration contract and application composition may need adaptation, but individual screens should not need to know about the replacement.

**Trade-offs:**

- We introduce an abstraction and some initial setup.
- The module must expose enough functionality for the application without leaking unnecessary library-specific details.
- A new navigation library may have a different mental model, so complete replacement without any changes is not guaranteed.

The objective is not to make navigation completely library-independent. It is to minimize the impact of changing it.

---

## 2. Networking as a Separate Core Module

**Decision:** Ktor client configuration and shared networking infrastructure live in `core:network`.

**Why?**

We do not want every feature to communicate with Ktor directly or configure its own HTTP client.

Instead, the core networking module owns the HTTP infrastructure and exposes a consistent way for feature data layers to perform network operations.

This gives us several benefits:

- We can replace Ktor with another HTTP client with minimal impact on feature implementations.
- Client configuration, engine selection, common headers, and shared networking behavior have one owner.
- Features do not need to duplicate client configuration.
- Networking infrastructure can evolve independently of business logic.

Feature data layers remain responsible for API-specific concerns, including request/response models, serialization decisions, and mapping responses into domain models.

For example, replacing Ktor with another HTTP library should primarily affect `core:network` and the integration points that expose its API. Feature business logic should not need to change.

**Trade-offs:**

- The core module needs a carefully designed API that supports different feature requirements.
- Over-abstraction could hide useful capabilities of the underlying HTTP library.
- Some library-specific details may still need to be exposed when they provide necessary functionality.

The objective is to isolate HTTP infrastructure, not to create a generic networking framework.

---

## 3. Feature-Oriented Modularization

**Decision:** Organize the application around features instead of creating a separate Gradle module for every architectural layer.

A feature can contain its own presentation, domain, and data packages. Separate Gradle modules are introduced when there is a clear reason to enforce an independent boundary or share functionality.

**Why?**

- Keeps related functionality together.
- Avoids excessive Gradle module creation.
- Makes feature ownership and dependencies easier to understand.
- Allows genuinely shared infrastructure to live independently.

For example, if two features need the same business logic or data implementation, we can extract that functionality into a shared module instead of duplicating it.

**Trade-offs:**

- Feature-internal layers do not have independent Gradle dependency boundaries.
- Some architectural rules are enforced by package organization and code conventions rather than Gradle.
- Shared functionality may require module extraction as the application grows.

The goal is to modularize based on actual boundaries, not simply create a module for every folder.

---

## 4. Shared as the Application Composition Root

**Decision:** The `shared` module assembles the application and connects its features and core infrastructure.

It is responsible for:

- App-level dependency injection.
- Registering available features.
- Selecting the start destination.
- Creating the navigation host.
- Providing the shared application entry point.

**Why?**

Features should not know which other features exist or how the complete application is assembled.

Keeping this responsibility in one place:

- Prevents circular dependencies between features and the application.
- Keeps feature modules independent of the app composition.
- Makes it clear where new features are integrated.
- Allows Android and iOS entry points to use the same application setup.

**Trade-offs:**

- `shared` depends on the features included in the application.
- Adding a feature requires registering it in the composition root.
- The composition root needs to be maintained as the application grows.

This dependency direction is intentional: the application depends on features, while features do not depend on the application.

---

## 5. Feature-Owned Navigation Registration

**Decision:** Each feature registers its own navigation entries and serializers through the `NavigationFeature` contract.

**Why?**

We want to avoid a central `when(destination)` statement that must be modified every time a new feature is introduced.

Instead:

- A feature owns its destination definitions.
- A feature owns the UI associated with those destinations.
- A feature provides its navigation entry registration.
- The application collects the registrations.

This means adding a feature does not require changing the navigation host's destination-rendering logic.

**Trade-offs:**

- Every feature has some registration boilerplate.
- The application still needs to include the feature in its registration list.
- The approach introduces a contract that needs to be maintained.

This complements the separate navigation module: `core:navigation` owns the navigation infrastructure, while features own their destinations.

---

## 6. Centralized Navigation Ownership

**Decision:** Screens do not directly manipulate the navigation back stack. They expose navigation callbacks, and the application delegates those actions to `AppNavigator`.

**Why?**

- Screens remain independent of the navigation library.
- Back-stack mutations have a single owner.
- Navigation behavior is easier to reason about.
- Screens can be previewed and tested without creating a real navigation environment.

**Trade-offs:**

- Navigation callbacks need to be passed to screens.
- The application layer coordinates navigation between features.
- Care is needed to avoid turning the navigator into a feature-specific business logic container.

---

## 7. Koin for Dependency Injection

**Decision:** Use Koin for dependency injection.

**Why?**

- Provides a relatively lightweight DI setup.
- Supports Kotlin Multiplatform.
- Allows dependencies to be organized into modules.
- Avoids requiring a compile-time code-generation workflow.

**Trade-offs:**

- Many dependency-wiring errors are discovered at runtime.
- Dependency definitions require discipline as the application grows.
- The application becomes coupled to Koin's configuration and APIs.

If dependency injection requirements change, the DI setup can be reconsidered independently of the feature business logic, provided dependencies are not exposed through Koin-specific APIs throughout the application.

---

## 8. Compose Multiplatform for Shared UI

**Decision:** Use Compose Multiplatform for the shared application UI.

**Why?**

- Allows Android and iOS to share screens and UI logic.
- Reduces duplicated presentation code.
- Keeps most UI development within the shared Kotlin codebase.

**Trade-offs:**

- Some platform-specific behavior still requires native implementations.
- Not every platform API can or should be shared.
- Platform-specific requirements must be handled through clear boundaries.

The objective is to share code where it makes sense without sacrificing native platform capabilities.

---

## 9. Platform Target Selection

**Decision:** Configure Android, `iosArm64`, and `iosSimulatorArm64` targets by default.

**Why?**

- `iosArm64` supports physical arm64 iOS devices.
- `iosSimulatorArm64` supports simulators on Apple Silicon Macs.
- The template targets modern Apple Silicon development environments.

**Trade-offs:**

- Intel Mac users need to add `iosX64` if they require the Intel simulator target.
- iOS builds require a working Xcode toolchain.
- Android build success does not guarantee iOS compilation or framework linking.

Additional targets can be introduced when required by the development environment.

---

## 10. Keep the Template Minimal

**Decision:** Include the core foundation and an example feature without prebuilding infrastructure that the application may never need.

**Why?**

- Keeps the template easy to understand.
- Avoids unnecessary dependencies and abstractions.
- Lets developers introduce complexity based on actual requirements.

**Trade-offs:**

The adopting project may need to introduce additional infrastructure, such as:

- Database and persistence.
- Authentication.
- Analytics and monitoring.
- Logging.
- Error-handling conventions.
- Testing infrastructure.

The template provides a starting point, not a complete production application.

---

## Final Principle

The main architectural principle behind this template is **isolation of responsibilities and replaceability of infrastructure**.

Features should focus on application functionality. Core modules should own reusable infrastructure. The application layer should assemble everything.

When an implementation changes, the impact should remain as localized as reasonably possible.

We do not aim to eliminate dependencies or abstractions entirely. We aim to make dependencies explicit and prevent infrastructure decisions from spreading unnecessarily throughout the project.