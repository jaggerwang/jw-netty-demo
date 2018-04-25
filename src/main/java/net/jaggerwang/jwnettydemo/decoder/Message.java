package net.jaggerwang.jwnettydemo.decoder;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Message {

    public static final short START_BYTE_FIRST = (short) 0x55;
    public static final short START_BYTE_SECOND = (short) 0xaa;

    private short startFirst;
    private short startSecond;
    private int length;
    private short version;
    private long deviceNo;
    private long time;
    private List<Long> datas = new ArrayList<>();
    private int checksum;

    public boolean isValid() {
        if (this.startFirst != this.START_BYTE_FIRST
                || this.startSecond != this.START_BYTE_SECOND
                || this.getVersion() != 1) {
            return false;
        }
        return true;
    }
}
