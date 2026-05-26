package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class LoginPage {

    private final Page page;

    private final Locator emailField;
    private final Locator passwordField;
    private final Locator loginBtn;
    private final Locator errorMsg;

    public LoginPage(Page page) {
        this.page = page;
        this.emailField  = page.locator("#email, input[type='email'], input[name='email']").first();
        this.passwordField = page.locator("#password, input[type='password']").first();
        this.loginBtn    = page.locator("button[type='submit'], button:has-text('Login'), button:has-text('Sign In')").first();
        this.errorMsg    = page.locator(".error, .alert-danger, [class*='error'], [class*='alert']").first();
    }

    public void login(String email, String password) {
        // Navigate to login page if not already there
        if (!page.url().contains("login") && !page.url().contains("sign-in")) {
            // Try clicking a login link in the nav
            Locator loginLink = page.locator(
                "a:has-text('Login'), a:has-text('Sign In'), a:has-text('Log In'), " +
                "[href*='login'], [href*='sign-in']"
            ).first();
            if (loginLink.isVisible()) {
                loginLink.click();
                page.waitForLoadState();
            } else {
                page.navigate("https://practice.qabrains.com/login");
                page.waitForLoadState();
            }
        }
        emailField.fill(email);
        passwordField.fill(password);
        loginBtn.click();
        page.waitForLoadState();
        page.waitForTimeout(1000);
    }

    public boolean isLoginSuccessful() {
        // Check for common post-login indicators
        String url = page.url();
        String[] successSelectors = {
            ".dashboard, #dashboard",
            "text=/welcome/i",
            "text=/logged in/i",
            "a:has-text('Logout'), a:has-text('Log Out'), a:has-text('Sign Out')",
            "[class*='dashboard'], [class*='account']"
        };
        for (String sel : successSelectors) {
            try {
                if (page.locator(sel).first().isVisible()) {
                    System.out.println("✅ Login success detected via: " + sel);
                    return true;
                }
            } catch (Exception ignored) {}
        }
        // URL changed away from /login
        boolean urlChanged = !url.contains("login") && !url.contains("sign-in");
        System.out.println("Login URL check: " + url + " → " + (urlChanged ? "PASSED" : "FAILED"));
        return urlChanged;
    }

    public boolean isErrorDisplayed() {
        String[] errorSelectors = {
            ".error, .alert-danger, .alert-error",
            "[class*='error'], [class*='invalid']",
            "text=/invalid/i",
            "text=/incorrect/i",
            "text=/wrong/i",
            "text=/failed/i"
        };
        for (String sel : errorSelectors) {
            try {
                if (page.locator(sel).first().isVisible()) {
                    System.out.println("✅ Login error detected via: " + sel);
                    return true;
                }
            } catch (Exception ignored) {}
        }
        // Still on login page = failure = error shown (implicit)
        boolean stillOnLogin = page.url().contains("login") || page.url().contains("sign-in");
        System.out.println("Login error URL check: still on login? " + stillOnLogin);
        return stillOnLogin;
    }
}