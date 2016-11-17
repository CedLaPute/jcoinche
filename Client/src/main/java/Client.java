import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * Created by héhéhéhéhéhéhéhé on 17/11/2016.
 */
public class Client extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext context, Object message) {
        ByteBuf in = (ByteBuf)message;

        try {
            // Lancer le parsing (classe custom)
            // Répondre avec la réponse appropriée

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
