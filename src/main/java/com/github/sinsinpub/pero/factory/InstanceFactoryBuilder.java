package com.github.sinsinpub.pero.factory;

/**
 * A builder (factory) of the instance factory.
 */
public class InstanceFactoryBuilder {

    private final String factoryProviderName;
    private static InstanceFactory firstProduct;

    InstanceFactoryBuilder(String providerName) {
        this.factoryProviderName = providerName;
    }

    /**
     * Preset of current available providers.
     * <p>
     * Old article:
     * http://www.christianschenk.org/blog/comparison-between-guice-picocontainer-and-spring/
     */
    public enum PresetProvider {
        /** Spring bean factory */
        SPRING(SpringBeanFactory.class.getName()),
        /** Guice injector */
        GUICE(GuiceBeanFactory.class.getName()),
        /** Tapestry IoC container */
        TAPESTRY(TapestryBeanFactory.class.getName()),
        /** Jodd Petite container */
        PETITE(PetiteBeanFactory.class.getName());
        private String className;

        private PresetProvider(String value) {
            this.className = value;
        }

        public String getClassName() {
            return className;
        }

    }

    /**
     * Create builder with provider name.
     * 
     * @param providerName provider name
     * @return new InstanceFactoryBuilder instance
     */
    public static InstanceFactoryBuilder builder(String providerName) {
        return new InstanceFactoryBuilder(providerName);
    }

    /**
     * Create builder with provider enum.
     * 
     * @param provider provider enum
     * @return new InstanceFactoryBuilder instance
     */
    public static InstanceFactoryBuilder builder(InstanceFactoryBuilder.PresetProvider provider) {
        return new InstanceFactoryBuilder(provider.getClassName());
    }

    /**
     * Build {@link InstanceFactory} provider instance by class name.
     * 
     * @return new InstanceFactory instance
     * @throws IllegalArgumentException when any class loading and instantiation exception occurs
     */
    public synchronized InstanceFactory build() {
        try {
            InstanceFactory product = (InstanceFactory) Class.forName(factoryProviderName)
                    .newInstance();
            if (firstProduct == null) {
                firstProduct = product;
            }
            return product;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.toString(), e);
        }
    }

    /**
     * @return First time built {@link InstanceFactory} instance.
     */
    public static InstanceFactory factory() {
        return firstProduct;
    }

}
