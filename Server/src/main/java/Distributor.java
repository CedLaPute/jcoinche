import io.netty.channel.socket.SocketChannel;

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

        for (int color = 0; color != 4; color++) {
            cards.add(new Card(7, 0, color)); // SEPT
            cards.add(new Card(8, 0, color)); // HUIT
            cards.add(new Card(9, 9, color)); // NEUF
            cards.add(new Card(10, 5, color)); // DIX
            cards.add(new Card(11, 14, color)); // VALET
            cards.add(new Card(12, 2, color)); // DAME
            cards.add(new Card(13, 3, color)); // ROI
            cards.add(new Card(14, 7, color)); // AS
        }
    }

    public Card getRandomCard() {
        int nb = (int)(Math.random() * 33);
        Card c;

        c = cards.get(nb);
        cards.remove(nb);
        return c;
    }

    public void Distribute(ArrayList<SocketChannel> _clients) {

        for (int i = 0; i < 4; i++) {

            for (int numberOfCardPerClient = 0; numberOfCardPerClient != 8; numberOfCardPerClient++) {

                Card todistribute = getRandomCard();
            }
        }

        System.out.print(cards.size());
    }
}
