package com.renato.service;

import com.renato.exception.BadMessageException;
import org.springframework.messaging.Message;

public class BadMessageValidator {

    public static final String BAD_MESSAGE = "bad message";

    public Message<String> handle(Message<String> message) throws BadMessageException {
        validate(message.getPayload());
        return message;
    }

    private void validate(String message) throws BadMessageException {
        if(BAD_MESSAGE.equals(message)) throw new BadMessageException();
    }

}
