package net.jaggerwang.jwnettydemo.decoder;

import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MessageDecoderTests {

    @Test
    void nettyTest() {
        EmbeddedChannel channel = new EmbeddedChannel(new MessageDecoder());
        channel.writeInbound(Unpooled.wrappedBuffer(new byte[]{
                // start
                (byte) 0x55, (byte) 0xaa,
                // length
                (byte) 0x00, (byte) 0x19,
                // version
                (byte) 0x01,
                // device no
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01,
                // time
                (byte) 0x5a, (byte) 0xdf, (byte) 0x37, (byte) 0x57,
                // datas
                (byte) 0x01, (byte) 0x0a,
                (byte) 0x02, (byte) 0x03, (byte) 0xe8,
                (byte) 0x04, (byte) 0x05, (byte) 0xf5, (byte) 0xe1, (byte) 0x00,
                // checksum
                (byte) 0x00, (byte) 0x00,
        }));
        Message msg = channel.readInbound();

        assertTrue(msg.isValid());
        assertEquals(0x55, msg.getStartFirst());
        assertEquals(0xaa, msg.getStartSecond());
        assertEquals(1, msg.getVersion());
        assertEquals(1, msg.getDeviceNo());
        assertEquals(1524578135, msg.getTime());
        assertEquals(10, (long) msg.getDatas().get(0));
        assertEquals(1000, (long) msg.getDatas().get(1));
        assertEquals(100000000, (long) msg.getDatas().get(2));
        assertEquals(0, msg.getChecksum());
    }
}
