package com.erudit;

import com.erudit.messages.Message;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.*;

/**
 * Created by zakhar on 17.04.2016.
 */
public class MessageEncoder implements Encoder.Text<Message> {

    public static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.findAndRegisterModules();
        MAPPER.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
    }

    @Override
    public String encode(Message message) throws EncodeException {
        try {
            return MessageEncoder.MAPPER.writeValueAsString(message);
        }
        catch(JsonProcessingException e) {
            throw new EncodeException(message, e.getMessage(), e);
        }
    }

    @Override
    public void init(EndpointConfig endpointConfig) { }

    @Override
    public void destroy() { }
}