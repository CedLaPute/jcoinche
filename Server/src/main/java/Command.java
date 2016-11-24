import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by héhéhéhéhéhéhéhé on 24/11/2016.
 */
public class Command {

    public ArrayList<Player> _players = new ArrayList<Player>();
    private Distributor deck;
    private Game _game;

    public Command() {
        this._game = new Game();
        deck = new Distributor();
        deck.generateAllCards();
    }

    public void analyse(ChannelHandlerContext ctx, String s) throws Exception {
        List<String> items = new ArrayList<String>(Arrays.asList(s.split("\r\n")));
        int playerIndex;

        for (int i = 0; i < items.size() - 1; i++) {

            List<String> data = new ArrayList<String>(Arrays.asList(items.get(i).split(" ")));

            if (data.get(0).compareTo("OK") == 0) {
                System.out.print("Received ok\n");
            }
            if (data.get(0).compareTo("LOGIN") == 0) {
                if (data.get(1).compareTo("\r\n") != 0) {
                    System.out.print("Login received : " + data.get(1) + "\n");
                    _players.add(new Player(ctx, data.get(1)));
                    _players.get(_players.size() - 1)._channel.writeAndFlush(new Serializer().sendOk());
                }
            }
            if (data.get(0).compareTo("READY") == 0) {
                if (data.get(1).compareTo("\r\n") != 0) {
                    System.out.print("Ready received, login : " + data.get(1) + "\n");
                    if ((playerIndex = this._game.getPlayerIndexByName(_players, data.get(1))) != -1) {
                        this._players.get(playerIndex)._ready = true;
                        this._players.get(playerIndex)._channel = ctx;
                        if (this._players.get(playerIndex)._ready == true) {
                            System.out.print("Client is ready\n");
                        }
                    }
                }
            }
            if (data.get(0).compareTo("CLIENTBET") == 0) {
                if (data.get(1).compareTo("\r\n") != 0 && data.get(2).compareTo("\r\n") != 0) {
                    System.out.print("Bet received from -" + data.get(1) + "-, of amount " + Integer.parseInt(data.get(2)) + "\n");
                    if ((playerIndex = this._game.getPlayerIndexByName(_players, data.get(1))) != -1) {
                        this._players.get(playerIndex)._bet = Integer.parseInt(data.get(2));
                        this._players.get(playerIndex)._channel = ctx;
                        if (this._players.get(playerIndex)._bet > -1) {
                            System.out.print("Client have bet\n");
                        }
                    }
                }
            }
            if (data.get(0).compareTo("PLAYCARD") == 0) {
                if (data.size() == 5) {
                    System.out.print("Card played from -" + data.get(1) + "-, " + data.get(2) + " " + data.get(3) + " " + data.get(4) + "\n");
                    if ((playerIndex = this._game.getPlayerIndexByName(_players, data.get(1))) != -1) {
                        Card c = new Card(Integer.parseInt(data.get(2)), Integer.parseInt(data.get(3)), Integer.parseInt(data.get(4)));
                        int playerTurn = this._game.getPlayerTurnByIndex(this._players);

                        this._game.removeCardFromPlayer(this._players.get(playerIndex), c);
                        this._players.get(playerIndex)._play = false;
                        this._players.get(playerIndex)._channel = ctx;
                        this._game.getGameInfo(_players);

                        for (int others = 0; others < this._players.size(); others++) {
                            if (others != playerIndex) {
                                this._players.get(others)._channel.writeAndFlush(new Serializer().getByteBufFromString(s)).sync();
                            }
                        }
                        if (this._game._over == false) {
                            this._players.get(playerTurn)._play = true;
                            this._players.get(playerTurn)._channel.writeAndFlush(new Serializer().sendPlay()).sync();
                        }
                    }
                }
            }
        }

        if (_players.size() == 4 && this._game._distribute == false) {
            deck.Distribute(_players);
            this._game._distribute = true;
        }

        if (this._game.allPlayersAreReady(_players) && this._game._bet == false) {
            for (playerIndex = 0; playerIndex < _players.size(); playerIndex++) {
                this._players.get(playerIndex)._channel.writeAndFlush(new Serializer().sendBet());
            }
            this._game._bet = true;
        }

        if (this._game.haveAllPlayersBet(_players) && this._game._playing == false) {
            this._players.get(0)._channel.writeAndFlush(new Serializer().sendPlay());
            this._players.get(0)._play = true;
            this._game._playing = true;
        }
    }
}
