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
package com.github.sinsinpub.pero.frontend;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.socks.SocksAuthResponse;
import io.netty.handler.codec.socks.SocksAuthScheme;
import io.netty.handler.codec.socks.SocksAuthStatus;
import io.netty.handler.codec.socks.SocksCmdRequest;
import io.netty.handler.codec.socks.SocksCmdRequestDecoder;
import io.netty.handler.codec.socks.SocksCmdType;
import io.netty.handler.codec.socks.SocksInitResponse;
import io.netty.handler.codec.socks.SocksRequest;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.internal.SystemPropertyUtil;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.github.sinsinpub.pero.config.AppProps;
import com.github.sinsinpub.pero.utils.NettyChannelUtils;

@PetiteBean
@Component
@Singleton
@ChannelHandler.Sharable
public final class SocksServerHandler extends SimpleChannelInboundHandler<SocksRequest> {

    private static final Logger logger = LoggerFactory.getLogger(SocksServerHandler.class);
    private static final int DEFAULT_EVENT_LOOP_THREADS = Math.max(1, SystemPropertyUtil.getInt(
            "io.netty.eventLoopThreads", Runtime.getRuntime().availableProcessors() * 2));
    @PetiteInject
    @Resource
    @Inject
    @Named("connectBackendHandler")
    private ChannelInboundHandler connectBackendHandler;
    private final EventExecutorGroup oioExecutorGroup;

    public SocksServerHandler() {
        int workerThreads = AppProps.PROPS.getInteger("worker.threads.max", 0);
        int executorThreads = AppProps.PROPS.getInteger("executor.threads.max", 0);
        if (executorThreads <= 0) {
            executorThreads = workerThreads <= 0 ? DEFAULT_EVENT_LOOP_THREADS : workerThreads;
        }
        oioExecutorGroup = new DefaultEventExecutorGroup(executorThreads,
                ThreadFactoryRepository.OIO_EXECUTOR_GROUP);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, SocksRequest socksRequest) throws Exception {
        switch (socksRequest.requestType()) {
        case INIT: {
            ctx.pipeline().addFirst(new SocksCmdRequestDecoder());
            ctx.write(new SocksInitResponse(SocksAuthScheme.NO_AUTH));
            break;
        }
        case AUTH:
            ctx.pipeline().addFirst(new SocksCmdRequestDecoder());
            ctx.write(new SocksAuthResponse(SocksAuthStatus.SUCCESS));
            break;
        case CMD:
            SocksCmdRequest req = (SocksCmdRequest) socksRequest;
            if (req.cmdType() == SocksCmdType.CONNECT) {
                ctx.pipeline().addLast(oioExecutorGroup, getConnectBackendHandler());
                ctx.pipeline().remove(this);
                ctx.fireChannelRead(socksRequest);
            } else {
                ctx.close();
            }
            break;
        case UNKNOWN:
            ctx.close();
            break;
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable) {
        logger.warn("Frontend exception caugh: {}", throwable.toString());
        NettyChannelUtils.closeOnFlush(ctx.channel());
    }

    public ChannelInboundHandler getConnectBackendHandler() {
        return connectBackendHandler;
    }

    public void setConnectBackendHandler(ChannelInboundHandler connectBackendHandler) {
        this.connectBackendHandler = connectBackendHandler;
    }

}
