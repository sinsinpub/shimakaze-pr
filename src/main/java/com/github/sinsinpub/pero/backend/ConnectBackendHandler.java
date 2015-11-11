/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.github.sinsinpub.pero.backend;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.socks.SocksCmdRequest;
import io.netty.handler.codec.socks.SocksCmdResponse;
import io.netty.handler.codec.socks.SocksCmdStatus;
import io.netty.handler.proxy.ProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;

import java.net.InetSocketAddress;

import javax.inject.Singleton;

import jodd.petite.meta.PetiteBean;
import jodd.util.StringUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.github.sinsinpub.pero.config.AppProps;
import com.github.sinsinpub.pero.utils.NettyChannelUtils;

@PetiteBean("connectBackendHandler")
@Component("connectBackendHandler")
@Singleton
@ChannelHandler.Sharable
public final class ConnectBackendHandler extends SimpleChannelInboundHandler<SocksCmdRequest> {

    private static final Logger logger = LoggerFactory.getLogger(ConnectBackendHandler.class);
    private final Bootstrap b = new Bootstrap();
    private final ProxyHandler upstreamProxyHandler;

    public ConnectBackendHandler() {
        String host = AppProps.PROPS.getValue("upstream.socks5.host");
        if (StringUtil.isEmpty(host)) {
            upstreamProxyHandler = null;
        } else {
            int port = AppProps.PROPS.getInteger("upstream.socks5.port", 1080);
            upstreamProxyHandler = new Socks5ProxyHandler(new InetSocketAddress(host, port));
        }
    }

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, final SocksCmdRequest request)
            throws Exception {
        final Promise<Channel> promise = ctx.executor().newPromise();
        promise.addListener(new GenericFutureListener<Future<Channel>>() {
            @Override
            public void operationComplete(final Future<Channel> future) throws Exception {
                final Channel outboundChannel = future.getNow();
                if (future.isSuccess()) {
                    ctx.channel()
                            .writeAndFlush(
                                    new SocksCmdResponse(SocksCmdStatus.SUCCESS,
                                            request.addressType()))
                            .addListener(new ChannelFutureListener() {
                                @Override
                                public void operationComplete(ChannelFuture channelFuture) {
                                    ctx.pipeline().remove(ConnectBackendHandler.this);
                                    outboundChannel.pipeline().addLast(
                                            new RelayTrafficHandler(ctx.channel()));
                                    ctx.pipeline().addLast(new RelayTrafficHandler(outboundChannel));
                                }
                            });
                } else {
                    ctx.channel().writeAndFlush(
                            new SocksCmdResponse(SocksCmdStatus.FAILURE, request.addressType()));
                    NettyChannelUtils.closeOnFlush(ctx.channel());
                }
            }
        });

        final Channel inboundChannel = ctx.channel();
        b.group(inboundChannel.eventLoop())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ProxyChannelInitializer(promise, upstreamProxyHandler));

        b.connect(request.host(), request.port()).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    // Connection established use handler provided results
                    if (upstreamProxyHandler == null) {
                        logger.info("Backend connected directly");
                    } else {
                        logger.info("Backend connected via: " + upstreamProxyHandler.proxyAddress());
                    }
                } else {
                    // Close the connection if the connection attempt has failed.
                    logger.info("Backend connection failed: " + future.cause(), future.cause());
                    ctx.channel().writeAndFlush(
                            new SocksCmdResponse(SocksCmdStatus.FAILURE, request.addressType()));
                    NettyChannelUtils.closeOnFlush(ctx.channel());
                }
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        NettyChannelUtils.closeOnFlush(ctx.channel());
    }

}
