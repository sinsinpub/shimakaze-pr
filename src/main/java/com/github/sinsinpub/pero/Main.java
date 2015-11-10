package com.github.sinsinpub.pero;

import java.util.Locale;
import java.util.TimeZone;

import com.github.sinsinpub.pero.factory.InstanceFactory;
import com.github.sinsinpub.pero.factory.PetiteBeanFactory;
import com.github.sinsinpub.pero.frontend.SocksServer;

/**
 * Current this proxy can be started with:
 * 
 * <pre>
 * java [-Dport={thisProxyPort} -Dsocks5Host={upstreamProxyHost} -Dsocks5Port={upstreamProxyPort}] -jar target/jar-with-dependencies.jar
 * </pre>
 * 
 * TODO more configurable
 */
public class Main {

    public static void main(String[] args) {
        initVmDefaults();
        try {
            InstanceFactory f = new PetiteBeanFactory();
            SocksServer server = f.getInstance(SocksServer.class, "socksServer");
            server.run();
        } catch (InterruptedException e) {
            System.out.println("Interrupted.");
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private static void initVmDefaults() {
        System.setProperty("file.encoding", "UTF-8");
        Locale.setDefault(Locale.US);
        TimeZone zone = TimeZone.getTimeZone("GMT+8");
        TimeZone.setDefault(zone);

        System.setProperty("io.netty.noJavassist", "true");
    }

}
