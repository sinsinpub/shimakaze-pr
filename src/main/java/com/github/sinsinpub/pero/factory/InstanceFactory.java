package com.github.sinsinpub.pero.factory;

/**
 * Lite JavaBean instances and dependencies control factory.
 * <p>
 * Any DI container such as spring, guice is welcomed.
 */
public interface InstanceFactory {

    /** Property name when configure implementation. */
    static final String FACTORY_PROP_NAME = "di.factory.name";

    <T> T getInstance(Class<T> beanClass);

    <T> T getInstance(Class<T> beanClass, String beanName);

    void start();

    void stop();

    void refresh();

    boolean isActive();

    long startupTime();

    long upTime();

}
