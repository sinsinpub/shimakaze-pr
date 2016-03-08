package com.github.sinsinpub.pero.frontend;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.socks.SocksCmdRequest;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@PetiteBean
@Component
@Singleton
@ChannelHandler.Sharable
public class ConnectBackendManager extends SimpleChannelInboundHandler<SocksCmdRequest> {

    private static final Logger logger = LoggerFactory.getLogger(SocksServerHandler.class);

    @PetiteInject
    @Resource
    @Inject
    @Named("connectBackendHandler")
    private ChannelInboundHandler connectBackendHandler;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SocksCmdRequest socksRequest)
            throws Exception {
        logger.info("// this=" + this);
        ctx.pipeline().addLast(connectBackendHandler);
        ctx.fireChannelRead(socksRequest);
    }

    void tryToConnect() {
        Bootstrap b = new Bootstrap();
        b.connect().addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    long nextRetryDelay = 1;
                    future.channel().eventLoop().schedule(new Runnable() {

                        @Override
                        public void run() {
                            tryToConnect();
                        }

                    }, nextRetryDelay, TimeUnit.SECONDS);
                }
            }

        });

    }

}
