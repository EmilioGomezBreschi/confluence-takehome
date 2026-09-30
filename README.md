# Confluence Take-Home Exercise

Java application developed for the Oxalis Senior Technical Specialist take-home exercise.

The application interacts with the Atlassian Confluence Cloud REST API to automate user, group, space, role, content, attachment, and page restriction management.

## Technologies

- Java 21
- OkHttp 4.12.0
- Jackson Databind 2.17.2
- Atlassian Confluence Cloud REST API
- IntelliJ IDEA

## Project Structure

```text
src/main/java/com/oxalis/confluencetakehome/
│
├── Main.java
│
├── config/
│   └── AtlassianConfig.java
│
├── client/
│   └── ConfluenceClient.java
│
└── service/
    ├── UserService.java
    ├── GroupService.java
    ├── SpaceService.java
    └── ContentService.java

src/main/resources/
└── sample.jpg
```

### Main Components

**AtlassianConfig**  
Loads the Atlassian configuration and credentials from environment variables.

**ConfluenceClient**  
Handles HTTP communication with the Atlassian APIs, including authentication, GET, POST, PUT, DELETE, multipart uploads, and error handling.

**UserService**  
Handles user invitations and user account lookup.

**GroupService**  
Creates groups and manages group membership.

**SpaceService**  
Creates spaces, retrieves roles and permissions, creates custom roles, and manages role assignments.

**ContentService**  
Creates pages, uploads attachments, embeds images, and manages page-level restrictions.

---

## Requirements

- Java 21
- Maven
- Atlassian Cloud account
- Dedicated Confluence test site
- Atlassian API token

---

## Environment Variables

The following environment variables must be configured before running the application:

```text
ATLASSIAN_BASE_URL
ATLASSIAN_EMAIL
ATLASSIAN_API_TOKEN
```

In IntelliJ IDEA they can be configured from:

```text
Run
→ Edit Configurations
→ Environment Variables
```

---

## Installation

Clone the repository:

```bash
git clone https://github.com/EmilioGomezBreschi/confluence-takehome.git
cd confluence-takehome
```

Verify Java:

```bash
java -version
```

Build the project:

### Windows

```powershell
.\mvnw.cmd clean package
```

### macOS / Linux

```bash
./mvnw clean package
```

Configure the required environment variables and run `Main.java`.

`Main.java` performs a connectivity and configuration check.
The provisioning operations are implemented in the service classes and were
executed incrementally during the exercise.

The workflow was intentionally executed in stages because some Confluence
operations are stateful and user invitations require account activation before
subsequent role assignments can be completed.

---

## Implemented Functionality

### User Management

The solution uses:

- 1 administrator account
- 4 standard users

The four standard users are invited through the Confluence REST API.

Their Atlassian `accountId` values are retrieved through the API and used for later operations.

### Group Management

A group named:

```text
oxalis-standard-users
```

is created.

All four standard users are added to the group.

The administrator account is not included.

---

## Collaborative Workspace

A private Confluence space named:

```text
Collaborative Workspace
```

is created.

The `oxalis-standard-users` group receives the built-in:

```text
Collaborator
```

role.

One standard user, `Emilio Gomez 1`, additionally receives the:

```text
Admin
```

role.

Final access model:

```text
Emilio Gomez 1 → Admin
Emilio Gomez 2 → Collaborator
Emilio Gomez 3 → Collaborator
Emilio Gomez 4 → Collaborator
```

---

## Restricted Workspace

A second private space named:

```text
Restricted Workspace
```

is created.

The Confluence environment used for this exercise operates in:

```json
{
  "mode": "ROLES"
}
```

Because the tenant uses the RBAC role model, legacy individual space permission updates are not supported.

To implement strict read-only access, a custom role was created:

```text
Oxalis Read Only
```

with only:

```text
read/space
```

The role is assigned to the `oxalis-standard-users` group.

The site administrator retains administrative access to the space.

---

## Page Content

A page named:

```text
Collaborative Workspace Overview
```

is created inside the Collaborative Workspace.

The page includes:

- A meaningful headline
- Body content
- An uploaded image
- An image embedded directly into the page content

The page content uses the Confluence `storage` representation.

The image is first uploaded as an attachment and then referenced from the page storage content.

---

## Page-Level Restriction

A read restriction is applied to the page so that only one specific user can access it.

Confluence prevents the authenticated API caller from applying a read restriction that would remove their own access.

Because of this API behavior, the authenticated administrator account is used as the single allowed user.

---

## Design Decisions

### Separation of Responsibilities

The project separates application logic from HTTP communication.

For example:

```text
ContentService
      ↓
ConfluenceClient
      ↓
Confluence REST API
```

Service classes describe what operation should be performed, while `ConfluenceClient` handles how requests are sent to Atlassian.

### Private Spaces

The first implementation used standard Confluence spaces.

After inspecting the resulting permissions, I found that the spaces inherited broad access for licensed users.

The implementation was changed to create private spaces instead and explicitly assign only the required roles.

This simplified the permission model and followed a least-privilege approach.

### RBAC

Initial attempts to modify legacy space permissions returned:

```text
Space permission updates that are not from RBAC
are not supported in roles-only mode.
```

The implementation was therefore adapted to use the current Confluence space role API.

### Custom Read-Only Role

The available system roles were inspected through the API.

The built-in `Viewer` role also allowed commenting, while the built-in `View only` role could not be assigned to the standard-user group in this environment.

A custom role containing only:

```text
read/space
```

was created to meet the read-only requirement precisely.

---

## Error Handling

HTTP requests are handled centrally by `ConfluenceClient`.

For unsuccessful responses, the application includes both:

- HTTP status code
- Atlassian response body

Java try-with-resources is used to ensure HTTP responses are properly closed.

---

## Security

- API credentials are stored in environment variables.
- `.idea`, build output, and local IDE files are excluded from Git.
- A dedicated Confluence test site was used.
- Private spaces are used to reduce inherited access.
- Access is granted explicitly through Confluence RBAC roles.

---

## AI Assistance

ChatGPT was used as a technical assistant during the exercise.

AI assistance included:

- Researching and validating some Confluence REST API endpoints
- Guidance on project structure
- Troubleshooting Confluence permissions
- Assistance with attachment upload
- Grammar checking

Important implementation decisions were made based on real API responses, including:

- Replacing standard spaces with private spaces
- Moving from legacy permissions to RBAC roles
- Creating a custom read-only role
- Adapting the page restriction strategy after receiving API validation errors

---

## Additional Documentation

A separate technical document contains the complete development process, including:

- API tests
- Screenshots
- Implementation steps
- Errors encountered
- Troubleshooting
- Design changes
- AI assistance references

---

## Author

**Emilio Gomez**