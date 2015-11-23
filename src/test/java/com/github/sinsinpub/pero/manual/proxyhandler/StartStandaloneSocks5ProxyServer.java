package com.github.sinsinpub.pero.manual.proxyhandler;

import org.junit.Test;

/**
 * Start a simple stand-alone SOCKSv5 proxy server.
 */
public class StartStandaloneSocks5ProxyServer {

    static final Integer PORT = 9080;

    @Test
    public void start() throws InterruptedException {
        final ProxyServer anonSocks5Proxy = new Socks5ProxyServer(PORT);
        anonSocks5Proxy.channel().closeFuture().sync();
    }

}
