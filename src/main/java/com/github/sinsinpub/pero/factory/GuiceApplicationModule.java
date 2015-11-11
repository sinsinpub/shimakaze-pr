package com.github.sinsinpub.pero.factory;

import io.netty.channel.ChannelInboundHandler;

import com.github.sinsinpub.pero.backend.ConnectBackendHandler;
import com.github.sinsinpub.pero.frontend.NettySocksServer;
import com.github.sinsinpub.pero.frontend.SocksServer;
import com.github.sinsinpub.pero.frontend.SocksServerHandler;
import com.github.sinsinpub.pero.frontend.SocksServerInitializer;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

/**
 * Google would like to maintain binding with `module` which the regular Java code works with your
 * IDE, and survives refactoring.
 * <p>
 * ClassPath scanning and auto-binding needs third-party extension.
 */
public class GuiceApplicationModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SocksServer.class).annotatedWith(Names.named(SocksServer.SOCKS_SERVER_BEAN_NAME)).to(
                NettySocksServer.class);
        bind(ChannelInboundHandler.class).annotatedWith(Names.named("socksServerInitializer")).to(
                SocksServerInitializer.class);
        bind(ChannelInboundHandler.class).annotatedWith(Names.named("socksServerHandler")).to(
                SocksServerHandler.class);
        bind(ChannelInboundHandler.class).annotatedWith(Names.named("connectBackendHandler")).to(
                ConnectBackendHandler.class);
    }

}
