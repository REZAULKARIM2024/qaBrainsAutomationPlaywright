package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class SearchPage {

    private final Page page;

    // ================= LOCATORS =================
    private final Locator searchBox;
    private final Locator searchBtn;

    // ================= CONSTRUCTOR =================
    public SearchPage(Page page) {

        this.page = page;

        this.searchBox = page.locator("#small-searchterms");
        this.searchBtn = page.locator("input[value='Search']");
    }

    // ================= ACTION =================
    public void searchItem(String item) {

        searchBox.fill(item);

        searchBtn.click();
    }
}