package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class HomePage {

    private final Page page;

    // ===== Locators =====
    private final Locator homeHeader;

    private final Locator catalogLink;
    private final Locator aboutLink;
    private final Locator blogLink;
    private final Locator wishListLink;
    private final Locator referFriendLink;

    private final Locator searchBox;
    private final Locator searchButton;
    private final Locator noResultMessage;

    // ===== Constructor =====
    public HomePage(Page page) {

        this.page = page;

        this.homeHeader = page.locator("header");

        this.catalogLink = page.locator("a[href*='catalog']");
        this.aboutLink = page.locator("a[href*='about']");
        this.blogLink = page.locator("a[href*='blog']");
        this.wishListLink = page.locator("a[href*='wishlist']");
        this.referFriendLink = page.locator("a[href*='refer']");

        this.searchBox = page.locator("[name='q']");
        this.searchButton = page.locator("button[type='submit']");

        this.noResultMessage = page.locator(
                "text=No results, text=no products"
        );
    }

    // ===== Navigation Actions =====
    public void clickCatalog() {

        catalogLink.click();
    }

    public void clickAbout() {

        aboutLink.click();
    }

    public void clickBlog() {

        blogLink.click();
    }

    public void clickWishList() {

        wishListLink.click();
    }

    public void clickReferFriend() {

        referFriendLink.click();
    }

    // ===== Search Actions =====
    public void enterSearchText(String text) {

        searchBox.fill(text);
    }

    public void clickSearchButton() {

        searchButton.click();
    }

    // ===== Validations =====
    public boolean isHomePageDisplayed() {

        return homeHeader.isVisible();
    }

    public boolean isNoResultDisplayed() {

        return noResultMessage.first().isVisible();
    }
}