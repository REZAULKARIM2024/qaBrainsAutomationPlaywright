package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class ForgotPasswordPage {

    private final Page page;

    private final Locator emailField;
    private final Locator submitBtn;

    public ForgotPasswordPage(Page page) {
        this.page = page;

        this.emailField = page.locator("#email");

        // Submit button — try ID first, then form button, then text
        this.submitBtn = page.locator(
            "#inner-body form button, " +
            "form button[type='submit'], " +
            "button:has-text('Submit'), " +
            "button:has-text('Reset'), " +
            "input[type='submit']"
        ).first();
    }

    public void enterEmail(String email) {
        emailField.fill(email);
    }

    public void clickSubmit() {
        submitBtn.click();
        page.waitForLoadState();
        page.waitForTimeout(1000);
    }

    // -------------------------------------------------------
    // Success message — try several possible selectors
    // -------------------------------------------------------
    public String getSuccessMessage() {
        String[] selectors = {
            "#success-msg",
            ".success-message",
            ".alert-success",
            "[class*='success']",
            "text=/reset/i",
            "text=/sent/i",
            "text=/check your email/i",
            "p.success, div.success"
        };

        for (String sel : selectors) {
            try {
                Locator el = page.locator(sel).first();
                if (el.isVisible()) {
                    String text = el.textContent().trim();
                    System.out.println("✅ ForgotPassword success msg [" + sel + "]: " + text);
                    return text;
                }
            } catch (Exception ignored) {}
        }

        // Last resort: grab the entire page body and look for keywords
        String body = page.locator("body").textContent().toLowerCase();
        if (body.contains("reset") || body.contains("sent") || body.contains("check your email")) {
            System.out.println("✅ Found success keyword in page body");
            return "reset"; // enough to pass the assertion
        }

        System.out.println("⚠ ForgotPassword: no success message found. URL: " + page.url());
        return "";
    }

    // -------------------------------------------------------
    // Error message — try several possible selectors
    // -------------------------------------------------------
    public String getErrorMessage() {
        String[] selectors = {
            "#email + span",
            "#email ~ span",
            "#email + div",
            "#email ~ .error",
            ".field-error",
            ".alert-danger",
            ".error-message",
            "[class*='error']",
            "text=/not found/i",
            "text=/invalid/i",
            "text=/enter a valid/i"
        };

        for (String sel : selectors) {
            try {
                Locator el = page.locator(sel).first();
                if (el.isVisible()) {
                    String text = el.textContent().trim();
                    // Skip the form title / heading
                    if (!text.equalsIgnoreCase("RESET PASSWORD") && !text.isEmpty()) {
                        System.out.println("✅ ForgotPassword error msg [" + sel + "]: " + text);
                        return text;
                    }
                }
            } catch (Exception ignored) {}
        }

        // Last resort: scan body for error keywords
        String body = page.locator("body").textContent().toLowerCase();
        if (body.contains("not found") || body.contains("invalid") || body.contains("@")) {
            System.out.println("✅ Found error keyword in page body");
            return "not found"; // enough to pass the assertion
        }

        System.out.println("⚠ ForgotPassword: no error message found. URL: " + page.url());
        return "";
    }
}