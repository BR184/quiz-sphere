package com.kl.quizsphere.config;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="https://github.com/BR184">BR184</a>
 * @version 1.0
 * @since 2024-09-09 19:48:42
 */
@Configuration
@Data
public class VipSchedulerConfig {

    @Bean
    public Scheduler vipScheduler() {
        ThreadFactory threadFactory = new ThreadFactory() {
            private final AtomicInteger threadNumber = new AtomicInteger();

            @Override
            public Thread newThread(@NotNull Runnable r) {
                Thread thread = new Thread(r, "VIPThreadPool-" + threadNumber.getAndIncrement());
                thread.setDaemon(false);
                return thread;
            }
        };

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10, threadFactory);
        return Schedulers.from(scheduledExecutorService);
    }
}
