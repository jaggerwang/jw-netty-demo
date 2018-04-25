package net.jaggerwang.jwnettydemo.handler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.jaggerwang.jwnettydemo.decoder.Message;
import net.jaggerwang.jwnettydemo.saver.Metric;
import net.jaggerwang.jwnettydemo.saver.MetricSaver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Sharable
public class MessageHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Message message = (Message) msg;
        if (!message.isValid()) {
            logger.error("invalid message: {}", message);
            ctx.close();
            return;
        }
        logger.debug("received message: {}", message);

        MetricSaver saver = new MetricSaver();
        Metric metric = new Metric(message);
        saver.save(metric);
        logger.debug("saved metric: {}", metric);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}