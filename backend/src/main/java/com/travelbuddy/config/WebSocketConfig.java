package com.travelbuddy.config;

import com.travelbuddy.websocket.WebSocketPresenceInterceptor;
import com.travelbuddy.security.WebSocketAuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	private final WebSocketAuthInterceptor authInterceptor;
	private final WebSocketPresenceInterceptor presenceInterceptor;

	public WebSocketConfig(WebSocketAuthInterceptor authInterceptor,
			@Lazy WebSocketPresenceInterceptor presenceInterceptor) {
		this.authInterceptor = authInterceptor;
		this.presenceInterceptor = presenceInterceptor;
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(authInterceptor, presenceInterceptor);
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/topic");
		config.setApplicationDestinationPrefixes("/app");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
	}
}
