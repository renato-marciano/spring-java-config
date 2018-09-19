package com.renato;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.jms.*;

import static com.renato.SpringIntegrationConfig.BROKER_URL;
import static com.renato.SpringIntegrationConfig.INBOUND_QUEUE;
import static com.renato.SpringIntegrationConfig.OUTPUT_QUEUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SimpleJmsITest {

    private static final int TIME_TO_WAIT_IN_MS = 3000;

    @Test
    public void consumeMessage_Within3Seconds_WhenMessageIsPublished() throws JMSException {
        String expectedMessage = "arbitrary message";
        Session session = createSession();

        sendMessage(expectedMessage, session);

        TextMessage textMsg = retrieveSentMessage(session);

        assertNotNull(textMsg);
        assertEquals(expectedMessage,textMsg.getText());
    }

    private Session createSession() throws JMSException {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
        Connection connection = connectionFactory.createConnection();
        connection.start();

        boolean isTransacted = false;
        return connection.createSession(isTransacted, Session.AUTO_ACKNOWLEDGE);
    }

    private void sendMessage(String expectedMessage, Session session) throws JMSException {
        Queue inboundQueue = session.createQueue(INBOUND_QUEUE);
        MessageProducer messageProducer = session.createProducer(inboundQueue);
        Message msg = session.createTextMessage(expectedMessage);
        messageProducer.send(msg);
    }

    private TextMessage retrieveSentMessage(Session session) throws JMSException {
        Queue outboundQueue = session.createQueue(OUTPUT_QUEUE);
        MessageConsumer messageConsumer = session.createConsumer(outboundQueue);
        return (TextMessage) messageConsumer.receive(TIME_TO_WAIT_IN_MS);
    }

}
