package com.github.sinsinpub.pero.frontend;

/**
 * SOCKSv5 server stands at front-end.
 */
public interface SocksServer {

    /** Socks server implementation name registered in DI container. */
    static final String SOCKS_SERVER_BEAN_NAME = "socksServer";

    /**
     * Runs until interrupted.
     * 
     * @throws InterruptedException
     */
    void run() throws InterruptedException;

}
