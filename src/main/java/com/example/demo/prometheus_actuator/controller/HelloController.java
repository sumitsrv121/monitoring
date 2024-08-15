package com.example.demo.prometheus_actuator.controller;

import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.Timer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class HelloController {

    private Counter visitCounter;
    private Timer timer;
    private DistributionSummary httpRequestDistributionHistogram;

    private List<Integer> queue = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));

    public HelloController(MeterRegistry registry) {
        visitCounter = Counter.builder("visit_counter")
                .tags("counter-tag", "visitors")
                .description("Number of visits to API")
                .register(registry);

        timer = Timer.builder("custom_time_recorder")
                .tags("timer-tag", "visitors")
                .description("Time required for the API")
                .register(registry);

        Gauge.builder("queue_size", queue, List::size).register(registry);

        httpRequestDistributionHistogram = DistributionSummary
                .builder("http_request_histogram_example")
                .description("Histogram example")
                .publishPercentileHistogram()
                .register(registry);
    }



    @GetMapping(path = "/visitApi")
    public Map<String, Double> greet() {
        visitCounter.increment();
        return Map.of("visit API", visitCounter.count());
    }

    @GetMapping(path = "/getResponseTime")
    public String timerExample() throws InterruptedException {
        Timer.Sample start = Timer.start();

        System.out.println("Doing some work");
        Thread.sleep(getRandomNumber(500, 1000));

        if(!queue.isEmpty()) {
            queue.remove(0);
        }

        long responseTimeInMilliSecond = timer.record(() -> start.stop(timer) / 1000000);
        System.out.println("Total time taken by API "+ responseTimeInMilliSecond);
        return "Response time for example API call: "+responseTimeInMilliSecond;
    }

    @GetMapping(path = "/getQueueSize")
    public String gaugeExample() {
        int number = getRandomNumber(500, 2000);
        queue.add(number);
        return "Gauge example api is called: "+queue.size();
    }

    @GetMapping(path = "/histogram")
    public String histogramExample() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        Thread.sleep(getRandomNumber(10, 1000));
        long duration = System.currentTimeMillis() - startTime;

        httpRequestDistributionHistogram.record(duration);
        return "histogram api is called: "+duration;
    }

    private int getRandomNumber(int i, int i1) {
        return (new Random().nextInt(i, i1));
    }
}
