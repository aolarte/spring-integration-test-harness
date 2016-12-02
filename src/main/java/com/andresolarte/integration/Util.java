package com.andresolarte.integration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Util {
    private static Log log = LogFactory.getLog(Util.class);

    public static Object threadInfo(Object o) {
        log.info("=====> Thread id: " + Thread.currentThread().getId());
        return o;
    }
}
