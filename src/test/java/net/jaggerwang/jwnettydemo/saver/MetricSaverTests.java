package net.jaggerwang.jwnettydemo.saver;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MetricSaverTests {

    @Test
    void saveTest() {
        Metric metric = new Metric();
        metric.setName("device_1");
        metric.setValue(100);
        metric.setTime(new Date());

        MetricSaver saver = new MetricSaver();
        boolean isSuccess = saver.save(metric);

        assertTrue(isSuccess);
    }
}
