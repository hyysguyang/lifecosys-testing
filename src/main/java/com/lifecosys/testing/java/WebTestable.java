package com.lifecosys.testing.java;

import com.google.common.base.Function;
import javaslang.Function0;
import javaslang.collection.HashMap;
import javaslang.collection.List;
import javaslang.collection.Map;
import javaslang.control.Option;
import javaslang.control.Try;
import org.fluentlenium.core.Fluent;
import org.fluentlenium.core.domain.FluentWebElement;
import org.fluentlenium.core.wait.FluentWait;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

/**
 * @author <a href="mailto:hyysguyang@gmail.com">Young Gu</a>
 */

public interface WebTestable extends Loggable {

    default Logger logger() {
        return LoggerFactory.getLogger(this.getClass().getName());
    }

    default Fluent self() {
        return (Fluent) this;
    }

    default FluentWebElement $(String selector) {
        return $$(selector).head();
    }

    default List<FluentWebElement> $$(String selector) {
        return List.ofAll(self().$(By.cssSelector(selector)));
    }

    default String getToatMessage() {
        return Try.of(() -> $$("#toast-container .toast-message").last().getText()).getOrElse("");
    }

    default void waitToast(String message) {
        wait(() -> message.equals(getToatMessage()));
    }

    default void waitToastMessageContain(String message) {
        wait(() -> getToatMessage().contains(message));
    }

    default void ignoreAlert(Function0<Fluent> click) {
        Try.of(() -> click.apply());
        waitCondition(ExpectedConditions.alertIsPresent());
        self().getDriver().switchTo().alert().accept();
    }

    default void clearToast() {
        self().executeScript("return window.toastr.clear();");
        waitCondition(invisibilityOfElementLocated(By.id("toast-container")));
    }

    default void blur(String selector) {

        StringContext sc = Companion.StringContext(HashMap.of("selector", selector));
        Function0<Object> blur = () -> self().executeScript(sc.s("return $('${selector}').blur();"));
        Function0<Boolean> isFocused = () -> self().executeScript(sc.s("return $('${selector}').is(':focus');")).getBooleanResult();
        Function0<Boolean> condition = () -> {
            if (isFocused.apply()) blur.apply();
            logger().debug("isFocused: " + isFocused.apply());
            return !isFocused.apply();
        };

        blur.apply();
        wait(condition);
    }

    long timeOutInSeconds = 30;

    default void wait(Function0<Boolean> condition) {
        waitCondition(input -> condition.apply());
    }

    default void waitInvisible(String selector) {
        waitCondition(invisibilityOfElementLocated(By.cssSelector(selector)));
    }

    default void waitDisplay(String selector) {
        waitCondition(visibilityOfElementLocated(By.cssSelector(selector)));
    }

    default void waitClickable(String selector) {
        waitCondition(elementToBeClickable(By.cssSelector(selector)));
    }

    default void waitClickable(String selector, String text) {
        waitCondition(textToBePresentInElementLocated(By.cssSelector(selector), text));
    }

    default FluentWait waiter() {
        return self().await().atMost(timeOutInSeconds, TimeUnit.SECONDS).ignoring(StaleElementReferenceException.class);
    }

    default <T> void waitCondition(ExpectedCondition<T> condition) {
        waiter().until((Function<? super Fluent, T>) flunt -> condition.apply(self().getDriver()));

    }

    default Map<String, String> seleniumEnv() {
        List<String> envList = List.of("DISPLAY", System.getProperty("lmportal.xvfb.id", ":1000"));
        return HashMap.of(envList.toJavaArray(String.class));
    }

    /**
     * To user headless, please start Xvfb first:
     * <p>
     * Xvfb :1000 -screen 0 1920x1080x24
     *
     * @return
     */
    default WebDriver createWebDriver() {

        if (Option.of(System.getProperty("webdriver.chrome.driver")).isEmpty()) {
            System.setProperty("webdriver.chrome.driver", "/Develop/tools/chromedriver/chromedriver");
        }

        ChromeDriverService.Builder builder = new ChromeDriverService.Builder();
        ChromeDriverService service = builder.usingAnyFreePort().withEnvironment(seleniumEnv().toJavaMap()).build();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        ChromeDriver driver = new ChromeDriver(service, options);
        driver.manage().window().setSize(new Dimension(1920, 1080));
        return driver;

    }

}
