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

import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.socks.SocksInitRequestDecoder;
import io.netty.handler.codec.socks.SocksMessageEncoder;

import javax.annotation.Resource;

import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;

import org.springframework.stereotype.Component;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@PetiteBean
@Component
@Singleton
public final class SocksServerInitializer extends ChannelInitializer<SocketChannel> {

    @PetiteInject
    @Resource
    @Inject
    @Named("socksServerHandler")
    private ChannelInboundHandler socksServerHandler;

    @Override
    public void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline p = socketChannel.pipeline();
        p.addLast(new SocksInitRequestDecoder());
        p.addLast(new SocksMessageEncoder());
        p.addLast(socksServerHandler);
    }

    public ChannelInboundHandler getSocksServerHandler() {
        return socksServerHandler;
    }

    public void setSocksServerHandler(ChannelInboundHandler socksServerHandler) {
        this.socksServerHandler = socksServerHandler;
    }

}
