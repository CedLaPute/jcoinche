import io.netty.channel.ChannelHandlerContext;

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
    public int _bet;

    public Player(ChannelHandlerContext channel, String login) {
        this._channel = channel;
        this._login = login;
    }
}
