package com.github.sinsinpub.pero.frontend;

import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.ThreadFactory;

class ThreadFactoryRepository {

    public static final ThreadFactory BOSS_GORUP = new DefaultThreadFactory("nettyBoss", false);
    public static final ThreadFactory WORKER_GROUP = new DefaultThreadFactory("frontendAcceptor",
            true);
    public static final ThreadFactory OIO_EXECUTOR_GROUP = new DefaultThreadFactory(
            "backendConnector", true);

}
