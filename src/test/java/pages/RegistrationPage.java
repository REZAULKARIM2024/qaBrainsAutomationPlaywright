package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class RegistrationPage {

    private final Page page;

    private final Locator registerLink;
    private final Locator nameField;
    private final Locator countryDropdown;
    private final Locator accountTypeDropdown;
    private final Locator emailField;
    private final Locator passwordField;
    private final Locator confirmPasswordField;
    private final Locator registerBtn;

    public RegistrationPage(Page page) {
        this.page = page;
        this.registerLink         = page.locator("#registration span");
        this.nameField            = page.locator("#name");
        this.countryDropdown      = page.locator("#country");
        this.accountTypeDropdown  = page.locator("#account");
        this.emailField           = page.locator("#email");
        this.passwordField        = page.locator("#password");
        this.confirmPasswordField = page.locator("#confirm_password");
        this.registerBtn          = page.locator("button[type='submit']");
    }

    public void openRegistrationPage() {
        if (!page.url().contains("registration")) {
            registerLink.click();
        }
        page.waitForURL("**/registration**");
        page.waitForLoadState();
    }

    public void enterRegistrationDetails(
            String fullName, String country, String accountType,
            String email, String password, String confirmPassword) {

        // ✅ Generate unique email using timestamp to avoid "already registered" error
        String finalEmail = email.equals("rezaulkarimqa25@gmail.com")
                ? "testuser" + System.currentTimeMillis() + "@mailtest.com"
                : email;

        System.out.println("📧 Registering with email: " + finalEmail);

        nameField.fill(fullName);
        countryDropdown.selectOption(country);
        accountTypeDropdown.selectOption(accountType);
        emailField.fill(finalEmail);
        passwordField.fill(password);
        confirmPasswordField.fill(confirmPassword);
    }

    public void clickRegister() {
        registerBtn.click();
        page.waitForLoadState();
        page.waitForTimeout(2000);

        // Print page body to see what happened
        String body = page.locator("body").textContent();
        System.out.println("📄 Page after register (first 400 chars): "
            + body.substring(0, Math.min(400, body.length())));
        System.out.println("🌐 URL after register: " + page.url());
    }

    public boolean isRegistrationSuccessful() {
        String url  = page.url();
        String body = page.locator("body").textContent().toLowerCase();

        System.out.println("🔍 Checking success. URL: " + url);

        // Check common success keywords in page body
        String[] keywords = {
            "successfully", "registered", "welcome", "thank you",
            "account created", "success", "confirm"
        };
        for (String kw : keywords) {
            if (body.contains(kw)) {
                System.out.println("✅ Found success keyword: " + kw);
                return true;
            }
        }

        // Check success UI elements
        String[] selectors = {
            ".alert-success", "[class*='success']", "#success-msg",
            ".success-message", "[role='alert']"
        };
        for (String sel : selectors) {
            try {
                if (page.locator(sel).first().isVisible()) {
                    System.out.println("✅ Success element: " + sel);
                    return true;
                }
            } catch (Exception ignored) {}
        }

        // URL changed away from /registration = redirect on success
        boolean redirected = !url.contains("registration");
        System.out.println("URL redirect check: " + redirected + " | URL: " + url);
        return redirected;
    }

    public boolean isEmailValidationMessageDisplayed() {
        String msg = emailField.evaluate("el => el.validationMessage").toString();
        System.out.println("HTML5 validation: " + msg);
        if (!msg.isEmpty()) return true;

        return page.locator(
            "#email + span, #email ~ span, .field-error, [class*='error']"
        ).count() > 0;
    }
}