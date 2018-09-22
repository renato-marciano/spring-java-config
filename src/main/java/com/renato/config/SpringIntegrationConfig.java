package com.renato.config;

import com.renato.service.BadMessageValidator;
import com.renato.service.ErrorService;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jms.ChannelPublishingJmsMessageListener;
import org.springframework.integration.jms.JmsMessageDrivenEndpoint;
import org.springframework.integration.jms.JmsSendingMessageHandler;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.messaging.MessageHandler;

import javax.jms.ConnectionFactory;

@Configuration
@EnableIntegration
@EnableMBeanExport
public class SpringIntegrationConfig {

    public static final String BROKER_URL = "vm://embedded-broker?broker.persistent=false";
    public static final String INBOUND_QUEUE = "inboundQueue";
    public static final String OUTPUT_QUEUE = "outputQueue";

    @Bean
    IntegrationFlow integrationFlow(){
        return IntegrationFlows.from(inboundJmsMessageDrivenEndpoint())
                .handle(badMessageValidator())
                .handle(outboundJmsMessageHandler())
                .get();
    }

    private JmsMessageDrivenEndpoint inboundJmsMessageDrivenEndpoint(){
        JmsMessageDrivenEndpoint jmsMessageDrivenEndpoint = new JmsMessageDrivenEndpoint(defaultMessageListenerContainer(), new ChannelPublishingJmsMessageListener());
        jmsMessageDrivenEndpoint.setErrorChannelName("errorChannel");
        return jmsMessageDrivenEndpoint ;
    }

    private BadMessageValidator badMessageValidator(){
        return new BadMessageValidator();
    }

    private MessageHandler outboundJmsMessageHandler(){
        JmsSendingMessageHandler handler = new JmsSendingMessageHandler(jmsTemplate());
        handler.setDestinationName(OUTPUT_QUEUE);
        return handler;
    }


    private DefaultMessageListenerContainer defaultMessageListenerContainer(){
        DefaultMessageListenerContainer defaultMessageListenerContainer = new DefaultMessageListenerContainer();
        defaultMessageListenerContainer.setConnectionFactory(connectionFactory());
        defaultMessageListenerContainer.setDestinationName(INBOUND_QUEUE);
        return defaultMessageListenerContainer;
    }

    @Bean
    @ServiceActivator(inputChannel = "errorChannel")
    ErrorService errorService(){
        return new ErrorService(jmsTemplate());
    }

    private JmsTemplate jmsTemplate(){
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(connectionFactory());
        return jmsTemplate;
    }

    private ConnectionFactory connectionFactory(){
        return new ActiveMQConnectionFactory(BROKER_URL);
    }

}
