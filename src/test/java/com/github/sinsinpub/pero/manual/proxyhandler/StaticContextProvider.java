package com.github.sinsinpub.pero.manual.proxyhandler;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.concurrent.DefaultThreadFactory;

@SuppressWarnings("deprecation")
final class StaticContextProvider {

    static final EventLoopGroup group = new NioEventLoopGroup(3, new DefaultThreadFactory("proxy",
            true));

    static final SslContext serverSslCtx;
    static final SslContext clientSslCtx;

    static {
        SslContext sctx;
        SslContext cctx;
        try {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sctx = SslContext.newServerContext(ssc.certificate(), ssc.privateKey());
            cctx = SslContext.newClientContext(InsecureTrustManagerFactory.INSTANCE);
        } catch (Exception e) {
            throw new Error(e);
        }
        serverSslCtx = sctx;
        clientSslCtx = cctx;
    }

}
