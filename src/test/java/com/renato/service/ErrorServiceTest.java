package com.renato.service;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import static org.junit.Assert.assertEquals;

public class ErrorServiceTest {

    @Test
    public void sendToErrorOutput(){
        JmsTemplate jmsTemplate = Mockito.mock(JmsTemplate.class);
        Exception exception = Mockito.mock(Exception.class);
        ArgumentCaptor<String> queue = ArgumentCaptor.forClass(String.class);

        ErrorService errorService = new ErrorService(jmsTemplate);
        errorService.handleError(exception);

        Mockito.verify(jmsTemplate).send(queue.capture(),Mockito.any(MessageCreator.class));
        assertEquals(queue.getValue(),"outputError");
    }
}
