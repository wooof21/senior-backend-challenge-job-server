package com.exmple.jobserver.adapters.external.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private final ExternalServiceProps props;

    @Bean
    public WebClient externalWebClient() {

        HttpClient httpClient = HttpClient.create()
                // TCP connect timeout
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,
                        (int) props.getTimeout().getConnect().toMillis())
                // Overall response timeout
                .responseTimeout(props.getTimeout().getResponse())
                // Read / write timeouts
                .doOnConnected(conn ->
                        conn.addHandlerLast(
                                        new ReadTimeoutHandler(
                                                props.getTimeout().getRead().toSeconds(),
                                                TimeUnit.SECONDS))
                                .addHandlerLast(
                                        new WriteTimeoutHandler(
                                                props.getTimeout().getWrite().toSeconds(),
                                                TimeUnit.SECONDS))
                );

        return WebClient.builder()
                .baseUrl(props.getBaseUrl())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
