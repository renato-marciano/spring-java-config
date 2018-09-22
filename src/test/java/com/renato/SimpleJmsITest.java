package com.renato;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.jms.*;

import static com.renato.config.SpringIntegrationConfig.*;
import static com.renato.service.BadMessageValidator.BAD_MESSAGE;
import static com.renato.service.ErrorService.EXCEPTION_WAS_HANDLED;
import static com.renato.service.ErrorService.OUTPUT_ERROR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SimpleJmsITest {

    private static final int TIME_TO_WAIT_IN_MS = 5000;


    @Test
    public void messageIsConsumed_WhenMessageIsGood() throws JMSException {
        String expectedMessage = "arbitrary message";
        Session session = createSession();

        sendMessage(expectedMessage, session);

        TextMessage textMsg = retrieveSentMessageFromOutputQueue(session);

        assertNotNull(textMsg);
        assertEquals(expectedMessage,textMsg.getText());
    }

    @Test
    public void handleException_WhenMessageIsBad() throws JMSException{
        Session session = createSession();

        sendMessage(BAD_MESSAGE, session);

        TextMessage textMsg = retrieveSentMessageFromErrorQueue(session);

        assertNotNull(textMsg);
        assertEquals(EXCEPTION_WAS_HANDLED,textMsg.getText());

    }

    private Session createSession() throws JMSException {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
        Connection connection = connectionFactory.createConnection();
        connection.start();
        return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    private void sendMessage(String expectedMessage, Session session) throws JMSException {
        Queue inboundQueue = session.createQueue(INBOUND_QUEUE);
        MessageProducer messageProducer = session.createProducer(inboundQueue);
        Message msg = session.createTextMessage(expectedMessage);
        messageProducer.send(msg);
    }

    private TextMessage retrieveSentMessageFromOutputQueue(Session session) throws JMSException {
        return retrieveSentMessageFromQueue(session,OUTPUT_QUEUE);

    }

    private TextMessage retrieveSentMessageFromErrorQueue(Session session) throws JMSException {
        return retrieveSentMessageFromQueue(session,OUTPUT_ERROR);
    }

    private TextMessage retrieveSentMessageFromQueue(Session session, String queue) throws JMSException {
        Queue outboundQueue = session.createQueue(queue);
        MessageConsumer messageConsumer = session.createConsumer(outboundQueue);
        return (TextMessage) messageConsumer.receive(TIME_TO_WAIT_IN_MS);
    }


}
