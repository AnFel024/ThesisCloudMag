package com.antithesis.cloudmag.client;

import com.timgroup.statsd.NonBlockingStatsDClientBuilder;
import com.timgroup.statsd.StatsDClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DogStatsdClient {

    public void sendMetric() {
        StatsDClient Statsd = new NonBlockingStatsDClientBuilder()
            .hostname("127.0.0.1")
            .port(8126)
            .maxPacketSizeBytes(1500)
            .build();

        Statsd.incrementCounter("example_metric.increment", "environment:dev");
        //Statsd.recordGaugeValue("example_metric.gauge", 100, ["environment:dev"]);
    }
}