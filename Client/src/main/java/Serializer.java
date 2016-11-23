import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Created by héhéhéhéhéhéhéhé on 21/11/2016.
 */
public class Serializer { // ENCODAGE ET DECODAGE DES STRING

    public Serializer() {}

    public String getStringFromBytebuf(ByteBuf b) {
        byte[] bites = new byte[b.capacity()];

        b.getBytes(0, bites);

        String s = new String(bites);
        return s;
    }

    public ByteBuf sendOk() {
        ByteBuf b;
        byte[] bites;
        String s = "OK\r\n";

        bites = s.getBytes();
        b = Unpooled.wrappedBuffer(bites);
        return b;
    }

    public ByteBuf sendLogin(String login) {
        ByteBuf b;
        byte[] bites;
        String s = "LOGIN " + login + "\r\n";

        bites = s.getBytes();
        b = Unpooled.wrappedBuffer(bites);
        return b;
    }

    public ByteBuf sendReady(String login) {
        ByteBuf b;
        byte[] bites;
        String s = "READY " + login + "\r\n";

        bites = s.getBytes();
        b = Unpooled.wrappedBuffer(bites);
        return b;
    }

    public ByteBuf sendPlayerCard(String login, Card c) {
        ByteBuf b;
        byte[] bites;
        String s = "PLAYCARD " + login + " " + c.getNumber() + " " + c.getValue() + " " + c.getColor() + "\r\n";

        bites = s.getBytes();
        b = Unpooled.wrappedBuffer(bites);
        return b;
    }
}
