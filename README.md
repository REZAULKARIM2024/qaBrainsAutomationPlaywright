# qaBrainsAutomationPlaywright

A end-to-end test automation framework built with **Java**, **Playwright**, **Cucumber (BDD)**, and **TestNG** for testing the [QA Brains Practice Site](https://practice.qabrains.com).

---

## 🛠️ Tech Stack

| Tool | Version | Purpose |
|---|---|---|
| Java | 17 | Programming language |
| Playwright | 1.53.0 | Browser automation |
| Cucumber | 7.15.0 | BDD / Gherkin feature files |
| TestNG | 7.9.0 | Test runner & assertions |
| Maven | 3.x | Build & dependency management |
| AssertJ | 3.25.3 | Fluent assertions |

---

## 📁 Project Structure

```
qaBrainsAutomationPlaywright/
│
├── src/
│   ├── main/java/
│   │   ├── pages/                  # Page Object Model classes
│   │   │   ├── CartPage.java
│   │   │   ├── ForgotPasswordPage.java
│   │   │   ├── LoginPage.java
│   │   │   ├── LogoutPage.java
│   │   │   └── RegistrationPage.java
│   │   │
│   │   └── utils/
│   │       └── BaseTest.java       # Playwright browser lifecycle
│   │
│   └── test/
│       ├── java/
│       │   ├── runners/
│       │   │   └── TestRunner.java # Cucumber + TestNG runner
│       │   │
│       │   └── stepdefinitions/
│       │       ├── CartSteps.java
│       │       ├── CommonSteps.java
│       │       ├── ForgotPasswordSteps.java
│       │       ├── Hooks.java
│       │       ├── LoginSteps.java
│       │       ├── LogoutSteps.java
│       │       ├── RegistrationSteps.java
│       │       ├── RegressionSteps.java
│       │       └── SmokeSteps.java
│       │
│       └── resources/
│           └── features/           # Gherkin feature files
│               ├── CartCheckout.feature
│               ├── ForgotPassword.feature
│               ├── Login.feature
│               ├── Logout.feature
│               ├── Registration.feature
│               ├── RegressionTests.feature
│               └── SmokeTests.feature
│
└── pom.xml
```

---

## ✅ Test Coverage

### 🔵 Smoke Tests (`@smoke`)
| Scenario | Description |
|---|---|
| S-01 | Verify home page loads |
| S-02 | Verify navigation links (QA Topics, Discussion, About Us) |
| S-03 | Verify Sign In page opens |
| Login | Login with valid credentials |
| Logout | Logged-in user can logout |
| Cart | Add product to cart |
| Registration | Successful user registration |
| Forgot Password | Registered user can reset password |

### 🟠 Regression Tests (`@regression`)
| Scenario | Description |
|---|---|
| R-01 | Add multiple products to cart |
| R-02 | Update product quantity |
| R-03 | Search for invalid item |
| Login | Login with invalid credentials |
| Cart | Remove product from cart |
| Registration | Registration fails with invalid email |
| Forgot Password | Unregistered user cannot reset password |

---

## ⚙️ Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- **Chromium** (auto-downloaded by Playwright on first run)
- Internet connection (tests run against live site)

---

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/qaBrainsAutomationPlaywright.git
cd qaBrainsAutomationPlaywright
```

### 2. Install Playwright Browsers

```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install chromium"
```

### 3. Run All Tests

```bash
mvn test
```

### 4. Run Only Smoke Tests

```bash
mvn test -Dcucumber.filter.tags="@smoke"
```

### 5. Run Only Regression Tests

```bash
mvn test -Dcucumber.filter.tags="@regression"
```

### 6. Run a Specific Feature

```bash
mvn test -Dcucumber.filter.tags="@Login"
```

---

## 📊 Test Reports

After running tests, reports are generated at:

| Report | Location |
|---|---|
| HTML Report | `target/cucumber-reports.html` |
| JSON Report | `target/cucumber-reports/cucumber.json` |

Open the HTML report in any browser to view detailed results with step-by-step execution status.

---

## 🏗️ Architecture

### Page Object Model (POM)
Each page of the application has a dedicated class under `pages/` that encapsulates all locators and interactions. Step definitions only call page object methods — no raw Playwright code in steps.

### BDD with Cucumber
Test scenarios are written in plain English (Gherkin) inside `.feature` files, making them readable by non-technical stakeholders.

### Browser Lifecycle
`BaseTest.java` manages Playwright browser initialization and teardown as static methods. `Hooks.java` calls `initBrowser()` in `@Before` and `quitBrowser()` in `@After` — ensuring a fresh browser for every scenario.

```
@Before (Hooks)         → BaseTest.initBrowser()
  → Scenario runs
@After  (Hooks)         → BaseTest.quitBrowser()
```

---

## 🔐 Test Credentials

The following credentials are used for login-dependent tests:

| Field | Value |
|---|---|
| Email | `test@qabrains.com` |
| Password | `Password123` |

> ⚠️ These are practice site credentials only. Do not use real credentials in test code.

---

## 🐛 Known Limitations

- Registration tests use a **timestamp-generated email** on each run to avoid "already registered" conflicts.
- The ecommerce section requires login — `CartPage` handles this automatically.
- Tests run in **headed mode** (browser visible) by default. To run headless, set `.setHeadless(true)` in `BaseTest.java`.

---

## 👤 Author

**Rezaul Karim**  
QA Engineer | [QA Brains](https://qabrains.com)

---


This project is for educational and practice purposes only.
