package com.github.sinsinpub.pero;

import com.github.sinsinpub.pero.config.AppProps;
import com.github.sinsinpub.pero.factory.InstanceFactory;
import com.github.sinsinpub.pero.factory.InstanceFactoryBuilder;
import com.github.sinsinpub.pero.factory.InstanceFactoryBuilder.PresetProvider;
import com.github.sinsinpub.pero.frontend.SocksServer;

/**
 * Current this proxy can be started with:
 * 
 * <pre>
 * java -jar jar-with-dependencies.jar
 * </pre>
 */
public class Main {

    public static void main(String[] args) {
        try {
            String factoryName = AppProps.PROPS.get(InstanceFactory.FACTORY_PROP_NAME,
                    PresetProvider.PETITE.name());
            InstanceFactory f = InstanceFactoryBuilder.builder(
                    PresetProvider.valueOf(factoryName.toUpperCase())).build();
            SocksServer server = f.getInstance(SocksServer.class, SocksServer.SOCKS_SERVER_BEAN_NAME);
            server.run();
        } catch (InterruptedException e) {
            System.out.println("Interrupted.");
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

}
