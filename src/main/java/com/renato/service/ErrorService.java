package com.renato.service;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.util.ErrorHandler;


public class ErrorService implements ErrorHandler {


    public static final String EXCEPTION_WAS_HANDLED = "exception was handled";
    public static final String OUTPUT_ERROR = "outputError";
    private final JmsTemplate jmsTemplate;

    public ErrorService(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public void handleError(Throwable throwable) {
        jmsTemplate.send(OUTPUT_ERROR, session -> session.createTextMessage(EXCEPTION_WAS_HANDLED));
    }
}
