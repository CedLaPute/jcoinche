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

public class ServerHandler {

    private int port;
    private Command _commandHandler;

    public ServerHandler(int port) {
        this.port = port;
        this._commandHandler = new Command();
    }

    public void commandHandler(ChannelHandlerContext ctx, String s) throws Exception {

        this._commandHandler.analyse(ctx, s);
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