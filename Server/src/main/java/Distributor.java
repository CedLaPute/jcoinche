import io.netty.channel.socket.SocketChannel;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;

/**
 * Created by héhéhéhéhéhéhéhé on 18/11/2016.
 */

public class Distributor {

    private ArrayList<Card> cards;

    public Distributor() {
        cards = new ArrayList<Card>();
    }

    // Les valeurs des cartes sont TOUT ATOUTS (cf protocole)

    public void generateAllCards() {

        // COLOR 0 = PIQUE
        // COLOR 1 = TREFLE
        // COLOR 2 = CARREAU
        // COLOR 3 = COEUR

        for (int color = 0; color != 4; color++) {
            cards.add(new Card(7, 0, color)); // SEPT
            cards.add(new Card(8, 0, color)); // HUIT
            cards.add(new Card(9, 9, color)); // NEUF
            cards.add(new Card(10,   5, color)); // DIX
            cards.add(new Card(11, 14, color)); // VALET
            cards.add(new Card(12, 2, color)); // DAME
            cards.add(new Card(13, 3, color)); // ROI
            cards.add(new Card(14, 7, color)); // AS
        }
    }

    public Card getRandomCard() {
        int nb = (int)(Math.random() * cards.size());
        Card c;

        c = cards.get(nb);
        cards.remove(nb);
        return c;
    }

    public void Distribute(ArrayList<Player> _players) {

        int numberOfCardsDistributed = 0;

        while (numberOfCardsDistributed < 32) {

            for (int i = 0; i < 4; i++) {
                Card todistribute = getRandomCard();
                ByteBuf b = new Serializer().sendCard(todistribute);

                _players.get(i)._channel.writeAndFlush(b);
                numberOfCardsDistributed++;
            }
        }
    }
}
