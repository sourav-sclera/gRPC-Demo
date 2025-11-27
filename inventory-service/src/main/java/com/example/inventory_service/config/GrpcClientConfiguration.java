package com.example.inventory_service.config;

import com.example.inventory.grpc.InventoryServiceGrpc;
import com.example.inventory.grpc.OrderServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class GrpcClientConfiguration {

    private ManagedChannel channel;

    @Bean
    @Qualifier("grpcChannel")
    public ManagedChannel grpcChannel() {
        this.channel = ManagedChannelBuilder
                .forAddress("localhost", 9090)
                .usePlaintext()
                .keepAliveTime(30, TimeUnit.SECONDS)
                .keepAliveTimeout(5, TimeUnit.SECONDS)
                .keepAliveWithoutCalls(true)
                .idleTimeout(1, TimeUnit.HOURS)
                .enableRetry()
                .maxRetryAttempts(3)
                .build();

        return this.channel;
    }

    @PreDestroy
    public void shutdownChannel() {
        if (channel != null) {
            channel.shutdown();
            try {
                channel.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                channel.shutdownNow();
            }
        }
    }

    @Bean
    public OrderServiceGrpc.OrderServiceBlockingStub orderServiceStub(
            @Qualifier("grpcChannel") ManagedChannel channel) {
        return OrderServiceGrpc.newBlockingStub(channel)
                .withMaxInboundMessageSize(100 * 1024 * 1024)
                .withMaxOutboundMessageSize(100 * 1024 * 1024)
                .withWaitForReady();
    }
}
