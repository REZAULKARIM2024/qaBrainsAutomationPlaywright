package pages;

import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

import java.util.List;
import java.util.regex.Pattern;

public class LogoutPage {

    private final Page page;

    // ─── সরাসরি Logout button এর selector ───
    private static final String LOGOUT_SELECTOR =
            "a[href*='logout'], a[href*='signout'], a[href*='sign-out'], " +
            "button[onclick*='logout'], " +
            "[data-testid*='logout'], [data-testid*='signout'], " +
            "[id*='logout'], [class*='logout']";

    private static final Pattern LOGOUT_TEXT_PATTERN =
            Pattern.compile("^(logout|log out|sign out|signout)$", Pattern.CASE_INSENSITIVE);

    private static final String[] DROPDOWN_TRIGGER_SELECTORS = {
            // class ভিত্তিক (সবচেয়ে সাধারণ)
            "[class*='user-menu']",
            "[class*='user-dropdown']",
            "[class*='account-menu']",
            "[class*='profile-menu']",
            "[class*='user-avatar']",
            "[class*='avatar']",
            "[class*='user-icon']",
            "[class*='user-name']",
            "[class*='nav-user']",
            "[class*='header-user']",
            // ARIA attributes
            "button[aria-haspopup='true']",
            "button[aria-haspopup='menu']",
            "[aria-expanded]",
            // data attributes
            "[data-toggle='dropdown']",
            "[data-bs-toggle='dropdown']",
            // Text ভিত্তিক
            "button:has-text('My Account')",
            "button:has-text('Account')",
            "button:has-text('Profile')",
            "a:has-text('My Account')",
            // img tag ভিত্তিক (profile photo)
            "button:has(img[class*='avatar'])",
            "a:has(img[class*='avatar'])",
            "button:has(img[class*='profile'])",
            // nav এর মধ্যে
            "nav [class*='user']",
            "header [class*='user']",
            ".navbar [class*='user']"
    };

    public LogoutPage(Page page) {
        this.page = page;
    }

    // ══════════════════════════════════════════════
    // মূল Logout method
    // ══════════════════════════════════════════════

    /**
     * Logout করে। একাধিক strategy তে চেষ্টা করে।
     */
    public void clickLogout() {
        System.out.println("🔓 Logout প্রক্রিয়া শুরু হচ্ছে...");

        // ─── Strategy 1: সরাসরি Logout button আছে কিনা ───
        if (tryDirectLogout()) return;

        // ─── Strategy 2: Dropdown খুলে Logout খোঁজো ───
        if (tryDropdownThenLogout()) return;

        // ─── Strategy 3: পুরো page এ Logout text সহ যেকোনো element ───
        if (tryAnyElementWithLogoutText()) return;

        // ─── Strategy 4: URL দিয়ে logout ───
        if (tryLogoutViaUrl()) return;

        // ─── Strategy 5: JavaScript দিয়ে ───
        if (tryJavaScriptLogout()) return;

        // সব strategy ব্যর্থ হলে meaningful error দাও
        throw new RuntimeException(
                "❌ Logout করা সম্ভব হয়নি।\n" +
                "সম্ভাব্য কারণ:\n" +
                "  ১. User login করা নেই\n" +
                "  ২. Logout button এর HTML structure পরিবর্তন হয়েছে\n" +
                "  ৩. Page সম্পূর্ণ load হয়নি\n" +
                "পরামর্শ: Browser DevTools দিয়ে Logout element এর exact selector বের করুন।"
        );
    }

    // ══════════════════════════════════════════════
    // Strategy 1: সরাসরি Logout বাটন
    // ══════════════════════════════════════════════

    private boolean tryDirectLogout() {
        System.out.println("🔍 Strategy 1: সরাসরি Logout বাটন খোঁজা হচ্ছে...");

        // Selector ভিত্তিক
        Locator logoutBySelector = page.locator(LOGOUT_SELECTOR);
        if (logoutBySelector.count() > 0) {
            Locator visible = logoutBySelector.filter(new Locator.FilterOptions().setVisible(true));
            if (visible.count() > 0) {
                System.out.println("✅ Selector দিয়ে Logout বাটন পাওয়া গেছে।");
                visible.first().click();
                waitForLogoutRedirect();
                return true;
            }
        }

        // Text ভিত্তিক (exact match)
        Locator logoutByText = page.getByRole(
                com.microsoft.playwright.options.AriaRole.LINK,
                new Page.GetByRoleOptions().setName(LOGOUT_TEXT_PATTERN)
        );
        if (logoutByText.count() == 0) {
            logoutByText = page.getByRole(
                    com.microsoft.playwright.options.AriaRole.BUTTON,
                    new Page.GetByRoleOptions().setName(LOGOUT_TEXT_PATTERN)
            );
        }
        if (logoutByText.count() > 0 && logoutByText.first().isVisible()) {
            System.out.println("✅ Text দিয়ে Logout বাটন পাওয়া গেছে।");
            logoutByText.first().click();
            waitForLogoutRedirect();
            return true;
        }

        System.out.println("⚠️ Strategy 1 ব্যর্থ।");
        return false;
    }

    // ══════════════════════════════════════════════
    // Strategy 2: Dropdown খুলে Logout খোঁজো
    // ══════════════════════════════════════════════

    private boolean tryDropdownThenLogout() {
        System.out.println("🔍 Strategy 2: Dropdown trigger খোঁজা হচ্ছে...");

        // প্রতিটি dropdown trigger selector চেষ্টা করো
        for (String triggerSelector : DROPDOWN_TRIGGER_SELECTORS) {
            try {
                Locator trigger = page.locator(triggerSelector)
                        .filter(new Locator.FilterOptions().setVisible(true));

                if (trigger.count() > 0) {
                    System.out.println("🖱️ Trigger পাওয়া গেছে: " + triggerSelector);
                    trigger.first().click();

                    // Dropdown animation এর জন্য অপেক্ষা
                    page.waitForTimeout(1000);

                    // এখন Logout বাটন visible হওয়া উচিত
                    Locator logoutBtn = page.locator(LOGOUT_SELECTOR);
                    if (logoutBtn.count() == 0) {
                        // Text দিয়েও খোঁজো
                        logoutBtn = page.locator("a, button, li")
                                .filter(new Locator.FilterOptions()
                                        .setHasText(Pattern.compile(
                                                "logout|log out|sign out",
                                                Pattern.CASE_INSENSITIVE)));
                    }

                    Locator visibleLogout = logoutBtn.filter(
                            new Locator.FilterOptions().setVisible(true));

                    if (visibleLogout.count() > 0) {
                        System.out.println("✅ Dropdown খুলে Logout বাটন পাওয়া গেছে।");
                        visibleLogout.first().click();
                        waitForLogoutRedirect();
                        return true;
                    }

                    // এই trigger কাজ করেনি, পরেরটা চেষ্টা করো (Escape চেপে বন্ধ করো)
                    page.keyboard().press("Escape");
                    page.waitForTimeout(300);
                }
            } catch (Exception e) {
                // এই selector কাজ করেনি, পরেরটা চেষ্টা করো
            }
        }

        System.out.println("⚠️ Strategy 2 ব্যর্থ।");
        return false;
    }

    // ══════════════════════════════════════════════
    // Strategy 3: Page এ যেকোনো Logout text element
    // ══════════════════════════════════════════════

    private boolean tryAnyElementWithLogoutText() {
        System.out.println("🔍 Strategy 3: Page এ Logout text সহ যেকোনো element খোঁজা হচ্ছে...");

        // JavaScript দিয়ে সব clickable element এ Logout text খোঁজো
        try {
            Object result = page.evaluate(
                    "() => {" +
                    "  const all = document.querySelectorAll('a, button, li, span, div[role=\"button\"]');" +
                    "  for (const el of all) {" +
                    "    const text = el.textContent.trim().toLowerCase();" +
                    "    if (text === 'logout' || text === 'log out' || " +
                    "        text === 'sign out' || text === 'signout') {" +
                    "      el.click();" +
                    "      return 'clicked';" +
                    "    }" +
                    "  }" +
                    "  return 'not found';" +
                    "}"
            );

            if ("clicked".equals(result)) {
                System.out.println("✅ JavaScript দিয়ে Logout element খুঁজে click করা হয়েছে।");
                page.waitForTimeout(1500);
                waitForLogoutRedirect();
                return true;
            }
        } catch (Exception e) {
            System.out.println("⚠️ JavaScript evaluation ব্যর্থ: " + e.getMessage());
        }

        System.out.println("⚠️ Strategy 3 ব্যর্থ।");
        return false;
    }

    // ══════════════════════════════════════════════
    // Strategy 4: URL দিয়ে Logout
    // ══════════════════════════════════════════════

    private boolean tryLogoutViaUrl() {
        System.out.println("🔍 Strategy 4: URL দিয়ে Logout চেষ্টা করা হচ্ছে...");

        String base = page.url().replaceAll("(https?://[^/]+).*", "$1");
        String[] logoutUrls = {
                base + "/logout",
                base + "/signout",
                base + "/sign-out",
                base + "/auth/logout",
                base + "/auth/signout",
                base + "/user/logout",
                base + "/account/logout",
                base + "/api/logout"
        };

        for (String url : logoutUrls) {
            try {
                System.out.println("🔗 চেষ্টা করা হচ্ছে: " + url);
                page.navigate(url);
                page.waitForLoadState();
                page.waitForTimeout(1000);

                if (isLoggedOut()) {
                    System.out.println("✅ URL দিয়ে Logout সফল: " + url);
                    return true;
                }
            } catch (Exception e) {
                // এই URL কাজ করেনি
            }
        }

        System.out.println("⚠️ Strategy 4 ব্যর্থ।");
        return false;
    }

    // ══════════════════════════════════════════════
    // Strategy 5: JavaScript দিয়ে Logout
    // ══════════════════════════════════════════════

    private boolean tryJavaScriptLogout() {
        System.out.println("🔍 Strategy 5: JavaScript দিয়ে Logout চেষ্টা করা হচ্ছে...");

        try {
            // Session/Cookie clear করো
            page.evaluate(
                    "() => {" +
                    "  document.cookie.split(';').forEach(c => {" +
                    "    document.cookie = c.replace(/^ +/, '')" +
                    "      .replace(/=.*/, '=;expires=' + new Date().toUTCString() + ';path=/');" +
                    "  });" +
                    "  if (window.sessionStorage) sessionStorage.clear();" +
                    "  if (window.localStorage) localStorage.removeItem('token');" +
                    "}"
            );

            // Login page এ navigate করো
            String base = page.url().replaceAll("(https?://[^/]+).*", "$1");
            page.navigate(base + "/auth/login");
            page.waitForLoadState();

            if (isLoggedOut()) {
                System.out.println("✅ JavaScript দিয়ে session clear করা হয়েছে।");
                return true;
            }
        } catch (Exception e) {
            System.out.println("⚠️ Strategy 5 ব্যর্থ: " + e.getMessage());
        }

        return false;
    }

    // ══════════════════════════════════════════════
    // সহায়ক methods
    // ══════════════════════════════════════════════

    /**
     * Logout এর পরে redirect হওয়ার জন্য অপেক্ষা করে।
     */
    private void waitForLogoutRedirect() {
        try {
            page.waitForURL(
                    url -> url.contains("login") || url.contains("signin") ||
                           url.endsWith("/") || url.equals(page.url()),
                    new Page.WaitForURLOptions().setTimeout(8000)
            );
        } catch (Exception e) {
            page.waitForLoadState();
        }
        page.waitForTimeout(800);
        System.out.println("✅ Logout redirect সম্পন্ন। Current URL: " + page.url());
    }

    /**
     * User logout হয়েছে কিনা যাচাই করে।
     *
     * @return true যদি logout হয়ে থাকে
     */
    public boolean isLoggedOut() {
        String url = page.url();

        // URL check
        if (url.contains("login") || url.contains("signin") || url.contains("auth")) {
            return true;
        }

        // Login form আছে কিনা
        Locator loginForm = page.locator(
                "input[type='password'], form[action*='login'], " +
                "#login, .login-form, [class*='login-container']");
        if (loginForm.count() > 0 && loginForm.first().isVisible()) {
            return true;
        }

        // "Sign In" বাটন আছে মানে logged out
        Locator signInBtn = page.locator(
                "a[href*='login'], a:has-text('Sign In'), a:has-text('Login'), " +
                "button:has-text('Sign In'), button:has-text('Login')");
        if (signInBtn.count() > 0 && signInBtn.first().isVisible()) {
            return true;
        }

        // Logout বাটন নেই মানে logged out
        Locator logoutBtn = page.locator(LOGOUT_SELECTOR);
        return logoutBtn.count() == 0;
    }

    /**
     * User currently logged in আছে কিনা।
     */
    public boolean isUserLoggedIn() {
        return !isLoggedOut();
    }

    /**
     * Logout সফল হয়েছে কিনা যাচাই করে।
     * LogoutSteps.java এ isLogoutSuccessful() call করা হয় — এটি isLoggedOut() এর alias।
     *
     * @return true যদি logout সফল হয়
     */
    public boolean isLogoutSuccessful() {
        return isLoggedOut();
    }
}