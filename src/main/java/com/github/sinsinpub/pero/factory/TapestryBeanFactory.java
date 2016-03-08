package com.github.sinsinpub.pero.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Yet another IoC implementation: Tapestry IoC container, formerly as HiveMind.
 */
public class TapestryBeanFactory implements InstanceFactory {

    private static final Logger logger = LoggerFactory.getLogger(TapestryBeanFactory.class);
    private String name = TapestryApplicationModule.class.getSimpleName();
    private long lastStartTime;

    @Override
    public <T> T getInstance(Class<T> beanClass) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T getInstance(Class<T> beanClass, String beanName) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public void start() {
        lastStartTime = System.currentTimeMillis();
        logger.info("{} started", name);
    }

    @Override
    public void stop() {
        logger.info("{} no need to stop", name);
    }

    @Override
    public void refresh() {
        logger.info("{} not support to refresh", name);
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public long startupTime() {
        return lastStartTime;
    }

    @Override
    public long upTime() {
        return System.currentTimeMillis() - lastStartTime;
    }

}
