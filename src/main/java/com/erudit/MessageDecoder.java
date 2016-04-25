package com.erudit;

import com.erudit.messages.ClientMessage;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by zakhar on 25.04.2016.
 */
public class MessageDecoder implements Decoder.Text<ClientMessage> {

    @Override
    public ClientMessage decode(String stringMessage) throws DecodeException {
        try {
            return MessageEncoder.MAPPER.readValue(stringMessage, ClientMessage.class);
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