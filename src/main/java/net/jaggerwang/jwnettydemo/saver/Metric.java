package net.jaggerwang.jwnettydemo.saver;

import lombok.Data;
import net.jaggerwang.jwnettydemo.decoder.Message;

import java.util.Date;
import java.util.OptionalDouble;

@Data
public class Metric {
    private String name;
    private int value;
    private Date time;

    public Metric() {

    }

    public Metric(Message message) {
        name = String.format("device_{}", message.getDeviceNo());

        OptionalDouble average = message.getDatas().stream().mapToDouble(e -> e).average();
        value = (int) (average.isPresent() ? average.getAsDouble() : 0);

        time = new Date(message.getTime());
    }
}
