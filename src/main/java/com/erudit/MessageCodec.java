package com.erudit;

import com.erudit.messages.Message;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.*;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by zakhar on 17.04.2016.
 */
public class MessageCodec implements Encoder.Text<Message>, Decoder.Text<Message> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.findAndRegisterModules();
        MAPPER.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
    }

    @Override
    public String encode(Message message) throws EncodeException
    {
        try
        {
            return MessageCodec.MAPPER.writeValueAsString(message);
        }
        catch(JsonProcessingException e)
        {
            throw new EncodeException(message, e.getMessage(), e);
        }
    }

    @Override
    public Message decode(String stringMessage) throws DecodeException {
        try {
            return MessageCodec.MAPPER.readValue(stringMessage, Message.class);
        }
        catch(IOException e) {
            throw new DecodeException((ByteBuffer)null, e.getMessage(), e);
        }
    }

    @Override
    public boolean willDecode(String s) {
        return true;
    }

    @Override
    public void init(EndpointConfig endpointConfig) { }

    @Override
    public void destroy() { }
}