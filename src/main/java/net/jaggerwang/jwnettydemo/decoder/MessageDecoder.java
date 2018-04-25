package net.jaggerwang.jwnettydemo.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class MessageDecoder extends ReplayingDecoder {

    private static final Logger logger = LogManager.getLogger();

    public static final int HEADER_LENGTH = 13;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        Message msg = new Message();

        msg.setStartFirst(in.readUnsignedByte());
        msg.setStartSecond(in.readUnsignedByte());
        logger.debug("decode start ok: {} {}", msg.getStartFirst(), msg.getStartSecond());

        msg.setLength(in.readUnsignedShort());
        logger.debug("decode length ok: {}", msg.getLength());

        msg.setVersion(in.readUnsignedByte());
        logger.debug("decode version ok: {}", msg.getVersion());

        msg.setDeviceNo(in.readUnsignedInt());
        logger.debug("decode device no ok: {}", msg.getDeviceNo());

        msg.setTime(in.readUnsignedInt());
        logger.debug("decode time ok: {}", msg.getTime());

        int pos = HEADER_LENGTH;
        while (pos < msg.getLength() - 2) {
            short len = in.readUnsignedByte();
            pos += 1;

            long data;
            switch (len) {
                case 1:
                    data = (long) in.readUnsignedByte();
                    pos += 1;
                    break;
                case 2:
                    data = (long) in.readUnsignedShort();
                    pos += 2;
                    break;
                case 4:
                    data = in.readUnsignedInt();
                    pos += 4;
                    break;
                default:
                    logger.error("unsupported data length: {}", len);
                    continue;
            }

            msg.getDatas().add(data);
            logger.debug("decode one data ok: {}", data);
        }

        msg.setChecksum(in.readUnsignedShort());
        logger.debug("decode checksum ok: {}", msg.getChecksum());

        out.add(msg);
    }
}
