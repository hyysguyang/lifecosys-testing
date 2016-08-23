package com.lifecosys.testing.java;

import javaslang.Function0;
import javaslang.collection.HashMap;
import javaslang.collection.List;
import javaslang.collection.Map;
import javaslang.control.Option;
import javaslang.control.Try;
import org.fluentlenium.adapter.FluentTest;
import org.fluentlenium.core.Fluent;
import org.fluentlenium.core.domain.FluentWebElement;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.screentaker.ViewportPastingStrategy;

import javax.imageio.ImageIO;
import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.support.ui.ExpectedConditions.*;

/**
 * Please start xvfb with below command before run Selenium firefox test. You
 * can add this command to /etc/rc.local on Ubuntu.
 *
 *
 * Xvfb :1000 -screen 0 1920x1080x24
 *
 * @author <a href="mailto:hyysguyang@gmail.com">Young Gu</a>
 * @author <a href="mailto:guyang@lansent.com">Young Gu</a>
 */
public abstract class BaseWebTests extends FluentTest {

	protected Logger logger = LoggerFactory
			.getLogger(this.getClass().getName());

	long timeOutInSeconds = 30;

	@Override
	public WebDriver getDefaultDriver() {
		return createWebDriver();
	}

	protected FluentWebElement $(String selector, String filterText) {
		return $$(selector)
				.filter(it -> it.getText().trim().equals(filterText)).get(0);
	}

	protected FluentWebElement $(String selector) {
		return super.$(By.cssSelector(selector)).get(0);
	}

	protected List<FluentWebElement> $$(String selector) {
		return List.ofAll(super.$(By.cssSelector(selector)));
	}

	protected String getToatMessage() {
		return Try.of(
				() -> $$("#toast-container .toast-message").last().getText())
				.getOrElse("");
	}

	protected void waitToast(String message) {
		wait(() -> message.equals(getToatMessage()));
	}

	protected void waitToastMessageContain(String message) {
		wait(() -> getToatMessage().contains(message));
	}

	protected void ignoreAlert(Function0<Fluent> click) {
		try {
			click.apply();
		} catch (Exception e) {

		}
		waitCondition(ExpectedConditions.alertIsPresent());
		getDriver().switchTo().alert().accept();
	}

	protected void clearToast() {
		executeJavascript("return window.toastr.clear();");
		waitCondition(invisibilityOfElementLocated(By.id("toast-container")));
	}

	protected void blur(String selector) {

		StringContext stringContext = Companion.StringContext(HashMap.of(
				"selector", selector));
		Function0<Object> blur = () -> executeJavascript(stringContext
				.s("return $('${selector}').blur();"));
		Function0<Boolean> isFocused = () -> executeJavascript(stringContext
				.s("return $('${selector}').is(':focus');"));
		Function0<Boolean> condition = () -> {
			if (isFocused.apply())
				blur.apply();
			logger.debug("isFocused: " + isFocused.apply());
			return !isFocused.apply();
		};

		blur.apply();
		wait(condition);
	}

	public void wait(Function0<Boolean> condition) {
		waitCondition(input -> condition.apply());
	}

	public <T> void waitCondition(ExpectedCondition<T> condition) {
		new WebDriverWait(getDriver(), timeOutInSeconds).ignoring(
				StaleElementReferenceException.class).until(condition);
	}

	public void waitInvisible(String selector) {
		waitCondition(invisibilityOfElementLocated(By.cssSelector(selector)));
	}

	public void waitDisplay(String selector) {
		waitCondition(visibilityOfElementLocated(By.cssSelector(selector)));
	}

	public void waitClickable(String selector) {
		waitCondition(elementToBeClickable(By.cssSelector(selector)));
	}

	public void waitClickable(String selector, String text) {
		waitCondition(textToBePresentInElementLocated(By.cssSelector(selector),
				text));
	}

	protected <T> T executeJavascript(String script) {
		return (T) ((JavascriptExecutor) getDriver()).executeScript(script);
	}

	protected void assertDisplayed(String selector) {
		assertThat($(selector).isDisplayed()).isTrue();
	}

	protected void assertText(String selector, String expectedText) {
		assertThat($(selector).getText()).isEqualTo(expectedText);
	}

	protected void assertAttribute(String selector, String name,
			String expectedValue) {
		assertThat($(selector).getAttribute(name)).isEqualTo(expectedValue);
	}

	protected Map<String, String> seleniumEnv() {
		List<String> envList = List.of("DISPLAY",
				System.getProperty("lmportal.xvfb.id", ":1000"));
		return HashMap.of(envList.toJavaArray(String.class));
	}

	/**
	 * To user headless, please start Xvfb first:
	 *
	 * Xvfb :1000 -screen 0 1920x1080x24
	 *
	 * @return
	 */
	protected WebDriver createWebDriver() {

		if (Option.of(System.getProperty("webdriver.chrome.driver")).isEmpty()) {
			System.setProperty("webdriver.chrome.driver",
					"/Develop/tools/chromedriver/chromedriver");
		}

		ChromeDriverService service = new ChromeDriverService.Builder()
				.usingAnyFreePort().withEnvironment(seleniumEnv().toJavaMap())
				.build();
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--start-maximized");
		ChromeDriver driver = new ChromeDriver(service, options);
		driver.manage().window().setSize(new Dimension(1920, 1080));
		return driver;

	}

	@Rule
	public TestRule testWatcher = new TestWatcher() {
		AShot aShot = new AShot().shootingStrategy(new ViewportPastingStrategy(
				500));

		public void failed(Throwable e, Description d) {
			String screenshotDir = Option.of(
					System.getProperty("SELENIUM_SCREENSHOT_DIR")).getOrElse(
					System.getProperty("user.home") + "/.selenium/screenshot");
			new File(screenshotDir).mkdirs();
			logger.info("Screenshot Dir: " + screenshotDir);
			e.printStackTrace();
			String fileFullPath = screenshotDir + "/"
					+ d.getTestClass().getSimpleName() + "-"
					+ d.getMethodName() + ".png";
			Try.of(() -> ImageIO.write(aShot.takeScreenshot(getDriver())
					.getImage(), "PNG", new File(fileFullPath)));
			e.printStackTrace();
			logger.info("Capture Screenshot: " + fileFullPath);
		}
	};

}