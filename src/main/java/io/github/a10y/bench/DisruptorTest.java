package io.github.a10y.bench;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

public class DisruptorTest {
    /**
     * Simple event that is a collection of keys + values.
     */
    static class SensorEvent {
        static final EventFactory<SensorEvent> FACTORY = SensorEvent::new;

        String key;
        long timestamp;
        double value;

        @Override
        public String toString() {
            return String.format("SensorEvent{key=%s timestamp=%d value=%g", key, timestamp, value);
        }
    }

    public static void main(String[] args) {
        Disruptor<SensorEvent> sensorEvents = new Disruptor<>(
                SensorEvent.FACTORY,
                2 << 2,
                new ThreadFactoryBuilder().setNameFormat("disruptor-%d").build(),
                ProducerType.SINGLE,
                new SleepingWaitStrategy());

        EventHandler<SensorEvent> handler1 = new SensorEventHandler("a");
        EventHandler<SensorEvent> handler2 = new SensorEventHandler("b");

        sensorEvents.getRingBuffer().addGatingSequences();
        sensorEvents.handleEventsWith(handler1)
                .then(handler2);

        RingBuffer<SensorEvent> ring = sensorEvents.start();

        for (int i = 0; i < 2 << 20; i++) {
            final int j = i;
            ring.publishEvent((event, seq) -> {
                event.key = "key-" + j;
                event.timestamp = j;
                event.value = j * Math.E;
            });
        }


        // The harder part is making others publish back into things...
    }

    static class SensorEventHandler implements EventHandler<SensorEvent> {
        private final String name;

        SensorEventHandler(String name) {
            this.name = name;
        }

        @Override
        public void onEvent(SensorEvent event, long sequence, boolean endOfBatch) {
            event.key = event.key + " and processed by " + name;
            System.err.println(event);
        }
    }
}
