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

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.github.sinsinpub.pero.ApplicationVersion;
import com.github.sinsinpub.pero.config.AppProps;

@PetiteBean(SocksServer.SOCKS_SERVER_BEAN_NAME)
@Component(SocksServer.SOCKS_SERVER_BEAN_NAME)
@Singleton
public final class NettySocksServer implements SocksServer {

    private static final Logger logger = LoggerFactory.getLogger(NettySocksServer.class);
    private int port = AppProps.PROPS.getInteger("socks.port", 1080);

    @PetiteInject
    @Resource
    @Inject
    @Named("socksServerInitializer")
    private ChannelInboundHandler socksServerInitializer;

    public NettySocksServer() {
    }

    public void run() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1, ThreadFactoryRepository.BOSS_GORUP);
        EventLoopGroup workerGroup = new NioEventLoopGroup(0, ThreadFactoryRepository.WORKER_GROUP);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(getSocksServerInitializer());
            ChannelFuture cf = b.bind(getPort()).sync();
            logger.info(String.format("Proxy server %s %s started.",
                    ApplicationVersion.DEFAULT.getApplicationName(),
                    ApplicationVersion.DEFAULT.getApplicationVersion()));
            cf.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public ChannelInboundHandler getSocksServerInitializer() {
        return socksServerInitializer;
    }

    public void setSocksServerInitializer(ChannelInboundHandler socksServerInitializer) {
        this.socksServerInitializer = socksServerInitializer;
    }

}
