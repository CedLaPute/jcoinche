/**
 * Created by héhéhéhéhéhéhéhé on 17/11/2016.
 */

import io.netty.bootstrap.ServerBootstrap;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ServerHandler {

    private int port;
    private Distributor deck;

    public ServerHandler(int port) {
        this.port = port;
        deck = new Distributor();
        deck.generateAllCards();

        Card c = deck.getRandomCard();
        System.out.print(c.getNumber() + " " + c.getValue() + " " + c.getColor());
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
                            ch.pipeline().addLast(new Server());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // Bind et debut d'acceptation de clients
            ChannelFuture f = b.bind(port).sync();

            System.out.print("Server initialization ok");

            // Attendre jusqua la fermerture du serveur
            f.channel().closeFuture().sync();
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
