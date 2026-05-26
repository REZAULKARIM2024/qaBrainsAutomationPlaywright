package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

public class CartPage {

    private final Page page;

    private static final String BASE_URL      = "https://practice.qabrains.com/ecommerce-site";
    private static final String LOGIN_URL     = "https://practice.qabrains.com/ecommerce/login";
    private static final String ECOMMERCE_URL = "https://practice.qabrains.com/ecommerce";

    private static final String EMAIL_XPATH        = "xpath=//*[@id='email']";
    private static final String PASSWORD_XPATH     = "xpath=//*[@id='password']";
    private static final String LOGIN_BUTTON_XPATH = "xpath=//button[contains(normalize-space(),'Login')]";
    private static final String VISIT_DEMO_XPATH   = "xpath=//a[contains(normalize-space(),'Visit Demo Site')]";

    private static final String ADD_TO_CART_XPATH =
        "xpath=//button[contains(translate(normalize-space()," +
        "'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'add to cart')]";

    private static final String REMOVE_XPATH =
        "xpath=//button[contains(translate(normalize-space()," +
        "'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'remove from cart')]";

    public CartPage(Page page) {
        this.page = page;
    }

    // -------------------------------------------------------
    // Login
    // -------------------------------------------------------
    private void openEcommerceAndLogin() {
        page.navigate(BASE_URL);
        page.waitForLoadState();

        Locator eCommerceSite = page.getByText("E-Commerce Site").first();
        if (eCommerceSite.count() > 0) {
            eCommerceSite.click();
            page.waitForLoadState();
        }

        Locator visitDemo = page.locator(VISIT_DEMO_XPATH).first();
        if (visitDemo.count() > 0) {
            visitDemo.click();
            page.waitForLoadState();
        } else {
            page.navigate(LOGIN_URL);
            page.waitForLoadState();
        }

        if (page.locator(EMAIL_XPATH).count() > 0) {
            page.locator(EMAIL_XPATH).fill("test@qabrains.com");
            page.locator(PASSWORD_XPATH).fill("Password123");
            page.locator(LOGIN_BUTTON_XPATH).click();
            page.waitForLoadState();
        }
    }

    // -------------------------------------------------------
    // Add product to cart
    // -------------------------------------------------------
    public void addProductToCart(int index) {
        openEcommerceAndLogin();

        try {
            page.locator(ADD_TO_CART_XPATH).first().waitFor(
                new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(10000)
            );
        } catch (Exception e) {
            throw new RuntimeException("'Add to Cart' button did not appear within 10 seconds.");
        }

        Locator addButton = page.locator(ADD_TO_CART_XPATH);
        addButton.nth(index).scrollIntoViewIfNeeded();
        addButton.nth(index).click();
        page.waitForTimeout(1500);

        // ✅ Wait until button changes to "Remove from cart"
        try {
            page.locator(REMOVE_XPATH).first().waitFor(
                new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(5000)
            );
        } catch (Exception e) {
            System.out.println("⚠ 'Remove from cart' did not appear after adding");
        }

        System.out.println("✅ Added product. Remove buttons: "
            + page.locator(REMOVE_XPATH).count());
    }

    // -------------------------------------------------------
    // Check if product is in cart
    // ✅ Always reload page first to get fresh state
    // -------------------------------------------------------
    public boolean isProductInCart(int index) {
        // Reload the ecommerce page to get fresh DOM
        page.navigate(ECOMMERCE_URL);
        page.waitForLoadState();
        page.waitForTimeout(1000);

        int removeCount = page.locator(REMOVE_XPATH).count();
        int addCount    = page.locator(ADD_TO_CART_XPATH).count();

        System.out.println("After reload → 'Remove from cart': " + removeCount
            + " | 'Add to cart': " + addCount);

        return removeCount > index;
    }

    // -------------------------------------------------------
    // Remove product from cart
    // -------------------------------------------------------
    public void removeProduct(int index) {
        // Make sure we are on ecommerce page
        if (!page.url().contains("ecommerce")) {
            page.navigate(ECOMMERCE_URL);
            page.waitForLoadState();
        }

        try {
            page.locator(REMOVE_XPATH).first().waitFor(
                new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(5000)
            );
        } catch (Exception e) {
            System.out.println("⚠ 'Remove from cart' not found within 5s");
        }

        Locator removeButtons = page.locator(REMOVE_XPATH);
        int count = removeButtons.count();
        System.out.println("'Remove from cart' buttons: " + count);

        if (count > index) {
            removeButtons.nth(index).scrollIntoViewIfNeeded();
            removeButtons.nth(index).click();
            page.waitForTimeout(1500);

            // ✅ Wait until button changes BACK to "Add to cart"
            try {
                page.locator(ADD_TO_CART_XPATH).first().waitFor(
                    new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(5000)
                );
            } catch (Exception e) {
                System.out.println("⚠ 'Add to cart' did not reappear after remove");
            }

            System.out.println("✅ Removed. Add buttons now: "
                + page.locator(ADD_TO_CART_XPATH).count());
        } else {
            throw new RuntimeException(
                "No 'Remove from cart' button at index " + index + ". Found: " + count
            );
        }
    }

    public void goToCheckout() {
        page.locator("xpath=//button[contains(normalize-space(),'Checkout')]").first().click();
    }

    public int getProductQuantity(int index) {
        Locator qty = page.locator(
            "xpath=//input[contains(@class,'quantity') or @name='quantity']"
        );
        if (qty.count() > index) {
            try {
                String value = qty.nth(index).inputValue();
                if (value != null && !value.isEmpty()) return Integer.parseInt(value);
            } catch (Exception ignored) {}
        }
        return 0;
    }
}