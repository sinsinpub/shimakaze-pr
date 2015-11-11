package com.github.sinsinpub.pero.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.name.Names;

/**
 * Yet another IoC implementation: Google Guice DI injector.
 */
public class GuiceBeanFactory implements InstanceFactory {

    private static final Logger logger = LoggerFactory.getLogger(GuiceBeanFactory.class);
    private final Injector injector;
    private String name = GuiceApplicationModule.class.getSimpleName();
    private long lastStartTime;

    public GuiceBeanFactory() {
        injector = Guice.createInjector(new GuiceApplicationModule());
        start();
    }

    @Override
    public <T> T getInstance(Class<T> beanClass) {
        return injector.getInstance(beanClass);
    }

    @Override
    public <T> T getInstance(Class<T> beanClass, String beanName) {
        return injector.getInstance(Key.get(beanClass, Names.named(beanName)));
    }

    public Injector addChildModule(Module... modules) {
        return injector.createChildInjector(modules);
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
