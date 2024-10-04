package com.example.aerospikedemo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.aerospike.client.AerospikeClient;

@Configuration
public class AerospikeConfig {

	@Value("${aerospike.host}")
	private String host;

	@Value("${aerospike.port}")
	private int port;

	@Bean
	public AerospikeClient aerospikeClient() {
		return new AerospikeClient(host, port);
	}
}
