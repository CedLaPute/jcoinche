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
}
