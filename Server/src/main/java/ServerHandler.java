/**
 * Created by héhéhéhéhéhéhéhé on 17/11/2016.
 */

import io.netty.bootstrap.ServerBootstrap;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ReferenceCountUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServerHandler {

    private int port;
    private boolean distributed = false;
    public ArrayList<Player> _players = new ArrayList<Player>();
    private Distributor deck;

    public ServerHandler(int port) {
        this.port = port;
        deck = new Distributor();
        deck.generateAllCards();
    }

    public int getPlayerIndexByName(String login) {
        int i = 0;

        while (i < _players.size()) {
            if (_players.get(i)._login.compareTo(login) == 0) {
                return i;
            }
        }
        return -1;
    }

    public boolean allPlayersAreReady() {
        for (int i = 0; i < _players.size(); i++) {
            if (_players.get(i)._ready == false)
                return false;
        }
        return true;
    }

    public void commandHandler(ChannelHandlerContext ctx, String s) throws Exception {
        List<String> items = new ArrayList<String>(Arrays.asList(s.split("\r\n")));
        int playerIndex;

        for (int i = 0; i < items.size() - 1; i++) {

            List<String> data = new ArrayList<String>(Arrays.asList(items.get(i).split(" ")));

            if (data.get(0).compareTo("OK") == 0) {
                System.out.print("Received ok\n");
            }
            if (data.get(0).compareTo("LOGIN") == 0) {
                System.out.print("Login received : " + data.get(1) + "\n");
                if (data.get(1).compareTo("\r\n") != 0) {
                    _players.add(new Player(ctx, data.get(1)));
                    _players.get(_players.size() - 1)._channel.writeAndFlush(new Serializer().sendOk());
                }
            }
            if (data.get(0).compareTo("READY") == 0) {
                System.out.print("Ready received, login : " + data.get(1) + "\n");
                if (data.get(1).compareTo("\r\n") != 0) {
                    if ((playerIndex = getPlayerIndexByName(data.get(1))) != -1) {
                        this._players.get(playerIndex)._ready = true;
                    }
                }
            }
        }

        if (_players.size() == 4 && distributed == false) {
            deck.Distribute(_players);
            distributed = true;
        }

        if (allPlayersAreReady()) {
            for (playerIndex = 0; playerIndex < _players.size(); playerIndex++) {
                this._players.get(playerIndex)._channel.writeAndFlush(new Serializer().sendBet());
            }
        }
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();

            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            // Acceptation de client
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    ByteBuf in = (ByteBuf)msg;

                                    try {
                                        // Lancer le parsing (module custom)
                                        // Répondre avec la réponse appropriée
                                        String s = new Serializer().getStringFromBytebuf(in);
                                        commandHandler(ctx, s);
                                    } finally {
                                        ReferenceCountUtil.release(msg);
                                    }
                                }

                                @Override
                                public void channelActive(ChannelHandlerContext context) throws Exception {
                                    System.out.print("Client has connected\n");
                                }
                            });
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // Bind et debut d'acceptation de clients
            ChannelFuture f = b.bind(port).sync();

            System.out.print("NettyServer initialization ok\n");

            f.channel().closeFuture().sync();

            System.out.print("ChannelFuture closed\n");

        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port;

        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 2727;
        }
        new ServerHandler(port).run();
    }

}