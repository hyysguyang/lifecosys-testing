import com.lifecosys.testing.java.AshotTestWatcher;
import com.lifecosys.testing.java.WebAssertable;
import com.lifecosys.testing.java.WebTestable;
import org.fluentlenium.adapter.FluentTest;
import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.annotation.Page;
import org.fluentlenium.core.annotation.PageUrl;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:hyysguyang@gmail.com">Young Gu</a>
 * @author <a href="mailto:guyang@lansent.com">Young Gu</a>
 */
public class WebTest extends FluentTest implements WebTestable, WebAssertable {

    @Page
    WebPage bingPage;

    @Test
    public void checkJava() throws Exception {
        goTo("https://www.yahoo.com/");

        await().until("input[name='p']").isClickable();
        assertThat($("input[name='p']").getText()).isEmpty();
        $("input[name='p']").text("java");
        assertThat($("input[name='p']").getValue()).isEqualTo("java");
        $("input[name='go']").click();
        await().until("a[href='http://www.java.com/']").isDisplayed();

        assertThat($("a[href='http://www.java.com/']").getText()).contains("Java");
        assertThat($("a[href='http://www.java.com/']").getText()).contains("Official Site");

    }

    @Test
    public void checkJavaWitPage() throws Exception {
        bingPage.go();
        bingPage.waitLoaded();
        assertThat(bingPage.getQueryText()).isEmpty();
        bingPage.fillSearchField("java");
        assertThat(bingPage.getQueryValue()).isEqualTo("java");
        bingPage.search();
        assertThat(bingPage.getJavaLinkText()).contains("Java");
        assertThat(bingPage.getJavaLinkText()).contains("Official Site");

    }


    @Override
    public WebDriver getDefaultDriver() {
        return createWebDriver();
    }

    @Rule public TestRule testWatcher = new AshotTestWatcher(getDriver());
}

@PageUrl("https://www.yahoo.com/")
class WebPage extends FluentPage implements WebTestable, WebAssertable {

    public String getQueryText() {
        return $("input[name='p']").getText();
    }

    public String getQueryValue() {
        return $("input[name='p']").getValue();
    }

    public String getJavaLinkText() {
        return $("a[href='http://www.java.com/']").getText();
    }

    public WebPage waitLoaded() {
        await().until("input[name='p']").isClickable();
        return this;
    }

    public WebPage fillSearchField(String value) {
        $("input[name='p']").text(value);
        return this;
    }

    public WebPage search() {
        $("[type='submit']").click();
        await().until("a[href='http://www.java.com/']").isDisplayed();
        return this;
    }

}
