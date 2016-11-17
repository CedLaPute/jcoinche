/**
 * Created by héhéhéhéhéhéhéhé on 17/11/2016.
 */

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class Server extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext context, Object message) {
        ByteBuf in = (ByteBuf)message;

        try {
            // Lancer le parsing (module custom)
            // Répondre avec la réponse appropriée
            System.out.print("in channelRead");
            while (in.isReadable()) {
                System.out.print((char)in.readByte());
            }
        } finally {
            ReferenceCountUtil.release(message);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        cause.printStackTrace();
        context.close();
    }

}
