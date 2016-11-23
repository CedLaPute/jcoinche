import io.netty.channel.ChannelHandlerContext;

/**
 * Created by héhéhéhéhéhéhéhé on 23/11/2016.
 */
public class Player {

    public ChannelHandlerContext _channel;
    private String _login;
    private int _team;
    private int _points;

    public Player(ChannelHandlerContext channel, String login) {
        this._channel = channel;
        this._login = login;
    }
}
