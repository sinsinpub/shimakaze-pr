package com.github.sinsinpub.pero.factory;

import jodd.petite.PetiteConfig;
import jodd.petite.PetiteContainer;
import jodd.petite.config.AutomagicPetiteConfigurator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mini IoC implementation: Jodd PetiteContainer.
 */
public class PetiteBeanFactory implements InstanceFactory {

    private static final Logger logger = LoggerFactory.getLogger(PetiteBeanFactory.class);
    private final PetiteContainer container;
    private String name = PetiteContainer.PETITE_CONTAINER_REF_NAME;
    private Thread shutdownHook;
    private boolean isStarted = false;
    private long lastStartTime;

    /**
     * Create and start PetiteContainer with default config.
     */
    public PetiteBeanFactory() {
        this.container = new PetiteContainer();
        start();
    }

    public PetiteBeanFactory(PetiteConfig config) {
        this.container = new PetiteContainer(config);
        start();
    }

    public synchronized void start() {
        if (isStarted) {
            return;
        }
        logger.info("Starting {}...", name);
        isStarted = true;
        lastStartTime = System.currentTimeMillis();
        doStart();
    }

    protected void doStart() {
        container.addSelf(name);
        AutomagicPetiteConfigurator petiteConfigurator = new AutomagicPetiteConfigurator();
        petiteConfigurator.configure(container);
        registerShutdownHook();
        logger.info(String.format("%s started up in %s ms.", name, upTime()));
    }

    public synchronized void stop() {
        if (!isStarted) {
            return;
        }
        logger.info("Stopping {}...", name);
        isStarted = false;
        doStop();
    }

    protected void doStop() {
        container.shutdown();
        logger.info("{} shut down.", name);
    }

    synchronized void registerShutdownHook() {
        if (this.shutdownHook == null) {
            this.shutdownHook = new Thread() {
                @Override
                public void run() {
                    doStop();
                }
            };
            Runtime.getRuntime().addShutdownHook(this.shutdownHook);
        }
    }

    @Override
    public <T> T getInstance(Class<T> beanClass) {
        return container.getBean(beanClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getInstance(Class<T> beanClass, String beanName) {
        return (T) container.getBean(beanName);
    }

    public void registerBean(Class<?> beanClass) {
        container.registerPetiteBean(beanClass, null, null, null, false);
    }

    public void addInstance(String beanName, Object beanInstance) {
        container.addBean(beanName, beanInstance);
    }

    @Override
    public void refresh() {
        logger.info("Refreshing {} on demand...", name);
        stop();
        start();
    }

    @Override
    public boolean isActive() {
        return isStarted;
    }

    @Override
    public long startupTime() {
        return lastStartTime;
    }

    @Override
    public long upTime() {
        return System.currentTimeMillis() - lastStartTime;
    }

    public String getName() {
        return name;
    }

}
