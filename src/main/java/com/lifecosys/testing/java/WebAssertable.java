package com.lifecosys.testing.java;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:hyysguyang@gmail.com">Young Gu</a>
 */

public interface WebAssertable {

    default WebTestable current() {
        return (WebTestable) this;
    }

    default void assertDisplayed(String selector) {
        assertThat(current().$(selector).isDisplayed()).isTrue();
    }

    default void assertText(String selector, String expectedText) {
        assertThat(current().$(selector).getText()).isEqualTo(expectedText);
    }

    default void assertAttribute(String selector, String name, String expectedValue) {
        assertThat(current().$(selector).getAttribute(name)).isEqualTo(expectedValue);
    }

}
