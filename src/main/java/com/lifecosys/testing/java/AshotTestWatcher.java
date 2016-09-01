package com.lifecosys.testing.java;

import javaslang.control.Option;
import javaslang.control.Try;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.WebDriver;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.screentaker.ViewportPastingStrategy;

import javax.imageio.ImageIO;
import java.io.File;

/**
 * @author <a href="mailto:hyysguyang@gmail.com">Young Gu</a>
 */

public class AshotTestWatcher extends TestWatcher implements Loggable {

    final private WebDriver webDriver;

    protected AShot aShot = new AShot().shootingStrategy(new ViewportPastingStrategy(500));

    public AshotTestWatcher(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public void failed(Throwable e, Description d) {
        logger().debug("Test failed: ", e);
        String defaultScreenshotDir = System.getProperty("user.home") + "/.selenium/screenshot";
        String screenshotDir = Option.of(System.getProperty("SELENIUM_SCREENSHOT_DIR")).getOrElse(defaultScreenshotDir);
        new File(screenshotDir).mkdirs();
        String fileFullPath = String.format("%s/%s-%s.png", screenshotDir, d.getTestClass().getSimpleName(), d.getMethodName());
        Try.of(() -> ImageIO.write(aShot.takeScreenshot(webDriver).getImage(), "PNG", new File(fileFullPath)));
        logger().info("Capture Screenshot: " + fileFullPath);
    }
}
