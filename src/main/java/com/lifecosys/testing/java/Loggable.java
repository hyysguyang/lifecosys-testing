package com.lifecosys.testing.java;

import org.fluentlenium.adapter.FluentTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:hyysguyang@gmail.com">Young Gu</a>
 */

public interface Loggable {
    default Logger logger() {
        return LoggerFactory.getLogger(this.getClass().getName());
    }
}
