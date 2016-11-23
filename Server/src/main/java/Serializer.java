import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.ArrayList;

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

    public ByteBuf sendDeck(ArrayList<Card> cards, String login) {
        ByteBuf b;
        byte[] bites;
        String s = "DECK " + login + " ";

        for (int i = 0; i < cards.size(); i++) {
            s += cards.get(i).getNumber() + " " + cards.get(i).getValue() + " " + cards.get(i).getColor() + " ";
        }
        s += "\r\n";

        System.out.print("Deck : " + s);

        bites = s.getBytes();
        b = Unpooled.wrappedBuffer(bites);
        return b;
    }

    public ByteBuf sendCard(Card _card) {
        ByteBuf b;
        byte[] bites;
        String s = "CARD " + _card.getNumber() + " " + _card.getValue() + " " + _card.getColor() + "\r\n";

        bites = s.getBytes();
        b = Unpooled.wrappedBuffer(bites);
        return b;
    }

    public ByteBuf sendBet() {
        ByteBuf b;
        byte[] bites;
        String s = "BET\r\n";

        bites = s.getBytes();
        b = Unpooled.wrappedBuffer(bites);
        return b;
    }
}
