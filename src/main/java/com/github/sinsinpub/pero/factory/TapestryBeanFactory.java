package com.github.sinsinpub.pero.factory;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Yet another IoC implementation: Tapestry IoC container, formerly known as HiveMind.
 */
public class TapestryBeanFactory implements InstanceFactory {

    private static final Logger logger = LoggerFactory.getLogger(TapestryBeanFactory.class);
    private final Registry registry;
    private String name = TapestryApplicationModule.class.getSimpleName();
    private long lastStartTime;

    public TapestryBeanFactory() {
        // Default modules needed? IOCUtilities.buildDefaultRegistry();
        RegistryBuilder builder = new RegistryBuilder();
        builder.add(TapestryApplicationModule.class);
        registry = builder.build();
        start();
    }

    @Override
    public <T> T getInstance(Class<T> beanClass) {
        return registry.getService(beanClass);
    }

    @Override
    public <T> T getInstance(Class<T> beanClass, String beanName) {
        return registry.getService(beanName, beanClass);
    }

    @Override
    public void start() {
        lastStartTime = System.currentTimeMillis();
        registry.performRegistryStartup();
        logger.info("{} started", name);
    }

    @Override
    public void stop() {
        logger.info("Stopping {}...", name);
        registry.shutdown();
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
