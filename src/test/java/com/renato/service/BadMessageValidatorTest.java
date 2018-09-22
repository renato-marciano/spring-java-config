package com.renato.service;

import com.renato.exception.BadMessageException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.messaging.support.MessageBuilder;

public class BadMessageValidatorTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void throwExceptionWhenMessageIsBad() throws BadMessageException {
        expectedException.expect(BadMessageException.class);
        BadMessageValidator badMessageValidator = new BadMessageValidator();
        badMessageValidator.handle(MessageBuilder.withPayload("bad message").build());
    }
}
