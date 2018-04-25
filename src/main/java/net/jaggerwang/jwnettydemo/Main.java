package net.jaggerwang.jwnettydemo;

import net.jaggerwang.jwnettydemo.config.ApplicationConfig;
import net.jaggerwang.jwnettydemo.decoder.MessageDecoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import net.jaggerwang.jwnettydemo.handler.MessageHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Main {

    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        final int port = Integer.parseInt(ApplicationConfig.getProperty("server.port"));
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline()
                                    .addLast(new MessageDecoder())
                                    .addLast(new MessageHandler());
                        }
                    });

            ChannelFuture f = b.bind(port).sync();
            logger.info("server started on port {}", port);

            f.channel().closeFuture().sync();
        } catch (Exception e) {
            logger.error("server exception", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            logger.info("server stopped");
        }
    }
}