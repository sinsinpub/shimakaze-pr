package com.github.sinsinpub.pero.factory;

import io.netty.channel.ChannelInboundHandler;

import org.apache.tapestry5.ioc.ServiceBinder;

import com.github.sinsinpub.pero.backend.ConnectBackendHandler;
import com.github.sinsinpub.pero.frontend.NettySocksServer;
import com.github.sinsinpub.pero.frontend.SocksServer;
import com.github.sinsinpub.pero.frontend.SocksServerHandler;
import com.github.sinsinpub.pero.frontend.SocksServerInitializer;

/**
 * Similar with Google Guice, using T5 IoC auto-binding configuration (no explicitly instantiating
 * builder).
 */
public final class TapestryApplicationModule {

    public static void bind(ServiceBinder binder) {
        // Preventing implementation class loaded in a new ClassLoader to trigger IllegalAccessError
        // when accessing other package private class.
        binder.bind(SocksServer.class, NettySocksServer.class).preventReloading();
        // The same service interface uses different service ids and matches the @Named annotation
        binder.bind(ChannelInboundHandler.class, SocksServerInitializer.class).withSimpleId();
        binder.bind(ChannelInboundHandler.class, SocksServerHandler.class).withSimpleId();
        binder.bind(ChannelInboundHandler.class, ConnectBackendHandler.class).withSimpleId();
    }

}
