import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;

/**
 * Created by héhéhéhéhéhéhéhé on 23/11/2016.
 */
public class Player {

    public ChannelHandlerContext _channel;
    public boolean _ready = false;
    public boolean _play = false;
    public String _login;
    public int _team;
    public int _points;
    public int _bet = -1;
    public ArrayList<Card> _cards = new ArrayList<Card>();

    public Player(ChannelHandlerContext channel, String login) {
        this._channel = channel;
        this._login = login;
    }

    public void addCard(Card c) {
        this._cards.add(c);
    }

    public void removeCard(int index) {
        if (this._cards.size() > index) {
            this._cards.remove(index);
        }
    }

    public int getCardByIndex(Card c) {
        if (this._cards.size() > 0) {
            for (int i = 0; i < this._cards.size(); i++) {
                if (this._cards.get(i).getNumber() == c.getNumber() && this._cards.get(i).getValue() == c.getValue() && this._cards.get(i).getColor() == c.getColor()) {
                    return i;
                }
            }
        }
        return -1;
    }
}
