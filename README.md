# OAuth2-Queddeng
OAuth2 Integration with GitHub &amp; Google



# Frontend:

Thymeleaf templates (home.html, profile.html) rendered by controllers.

# Backend Components:

Layer	Classes	Responsibilities
Security	SecurityConfig.java	Configures OAuth2 login for Google/GitHub, session management, CSRF protection, and secured routes (/profile).
Controllers	HomeController.java	Renders / (home page with login buttons).
ProfileController.java
Service	CustomOAuth2UserService.java	Handles OAuth2 login, fetches user info from provider, creates or updates User + AuthProvider in DB.
Repository	UserRepository.java, AuthProviderRepository.java	CRUD operations on User and AuthProvider entities.
Model	User.java, AuthProvider.java	Entity mapping for database tables.
Application Entry	Oauth2LoginApplication.java	Spring Boot main application class, runs the server.

# Database:

Stores User and AuthProvider records.

# OAuth2 Providers:

Google / GitHub provide user authentication and profile info.

# Flow

Browser requests / → HomeController renders home.html with login buttons.

User clicks Login with Google/GitHub → Spring Security (SecurityConfig) redirects to provider.

Provider authenticates → redirects back → CustomOAuth2UserService fetches user info.

Service checks database:

New user → create User + AuthProvider.

Existing user → load user.

Session is created → user redirected to /profile.

ProfileController handles GET/POST for profile.

/logout invalidates session → redirects to /.

# Class-Based Architecture Diagram (Text Version)

<img width="420" height="779" alt="image" src="https://github.com/user-attachments/assets/f86eea43-bd11-4e1b-b9da-eec994a49718" />
