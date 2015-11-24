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

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.MessageSizeEstimator;
import io.netty.util.ReferenceCountUtil;

import java.util.concurrent.atomic.AtomicLong;

import jodd.datetime.JStopWatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sinsinpub.pero.utils.NettyChannelUtils;

public final class RelayTrafficHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(RelayTrafficHandler.class);
    private final Channel relayChannel;
    private MessageSizeEstimator.Handle estimatorHandle;
    private final AtomicLong readBytes = new AtomicLong();
    private final JStopWatch watch = new JStopWatch();

    public RelayTrafficHandler(Channel relayChannel) {
        this.relayChannel = relayChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);
        watch.start();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (relayChannel.isActive()) {
            int size = estimatorHandle(relayChannel).size(msg);
            readBytes.addAndGet(size);
            relayChannel.writeAndFlush(msg);
        } else {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (relayChannel.isActive()) {
            NettyChannelUtils.closeOnFlush(relayChannel);
        }
        watch.stop();
        recordTransferLog();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        watch.stop();
        logger.error("Exception when relay traffic:", cause);
        ctx.close();
    }

    void recordTransferLog() {
        logger.info(String.format("%s, %s bytes transfered, lifetime %s millis.", relayChannel,
                readBytes.get(), watch.total()));
    }

    final MessageSizeEstimator.Handle estimatorHandle(Channel channel) {
        if (estimatorHandle == null) {
            estimatorHandle = channel.config().getMessageSizeEstimator().newHandle();
        }
        return estimatorHandle;
    }

}
