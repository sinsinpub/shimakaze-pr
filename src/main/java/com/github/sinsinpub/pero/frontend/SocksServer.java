package com.github.sinsinpub.pero.frontend;

/**
 * SOCKSv5 server stands at front-end.
 */
public interface SocksServer {

    /**
     * Runs until interrupted.
     * 
     * @throws InterruptedException
     */
    void run() throws InterruptedException;

}
