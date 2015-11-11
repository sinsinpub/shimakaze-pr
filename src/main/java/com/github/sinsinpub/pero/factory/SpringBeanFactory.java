package com.github.sinsinpub.pero.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.github.sinsinpub.pero.ApplicationVersion;

/**
 * Popular IoC implementation: Spring BeanFactory Annotation configuration.
 */
public class SpringBeanFactory implements InstanceFactory {

    private static final Logger logger = LoggerFactory.getLogger(SpringBeanFactory.class);
    private final AnnotationConfigApplicationContext applicationContext;

    /**
     * Create and start annotation config spring BeanFactory.
     */
    public SpringBeanFactory() {
        applicationContext = new AnnotationConfigApplicationContext(SpringApplicationConfig.class);
        start();
    }

    @Configuration
    @ComponentScan(basePackageClasses = ApplicationVersion.class)
    public static class SpringApplicationConfig {
    }

    @Override
    public <T> T getInstance(Class<T> beanClass) {
        return applicationContext.getBean(beanClass);
    }

    @Override
    public <T> T getInstance(Class<T> beanClass, String beanName) {
        return applicationContext.getBean(beanName, beanClass);
    }

    public void registerBean(Class<?> beanClass) {
        applicationContext.register(beanClass);
    }

    @Override
    public void start() {
        if (!isActive()) {
            applicationContext.refresh();
        }
        applicationContext.registerShutdownHook();
        logger.info("{} started", getName());
    }

    @Override
    public void stop() {
        if (isActive()) {
            applicationContext.close();
            logger.info("{} stopped", getName());
        }
    }

    @Override
    public void refresh() {
        applicationContext.refresh();
    }

    @Override
    public boolean isActive() {
        return applicationContext.isActive();
    }

    @Override
    public long startupTime() {
        return applicationContext.getStartupDate();
    }

    @Override
    public long upTime() {
        return System.currentTimeMillis() - applicationContext.getStartupDate();
    }

    public String getName() {
        return applicationContext.getDisplayName();
    }

}
