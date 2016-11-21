import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by héhéhéhéhéhéhéhé on 17/11/2016.
 */
public class Client extends ChannelInboundHandlerAdapter {

    private ArrayList<Card> _cards = new ArrayList<Card>();

    @Override
    public void channelRead(ChannelHandlerContext context, Object message) {
        ByteBuf in = (ByteBuf)message;

        try {
            // Lancer le parsing (classe custom)
            // Répondre avec la réponse appropriée
            String s = new Serializer().getStringFromBytebuf(in);
            analyseCommand(context, s);
        } finally {
            ReferenceCountUtil.release(message);
            in.clear();
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext context) throws Exception {
        context.writeAndFlush(new Serializer().sendOk());
    }

    @Override
    public void channelInactive(ChannelHandlerContext context) throws Exception {
        System.out.print("Disconnected\n");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        cause.printStackTrace();
        context.close();
    }

    public void analyseCommand(ChannelHandlerContext context, String s) {
        List<String> items = new ArrayList<String>(Arrays.asList(s.split("\r\n")));

        for (int i = 0; i < items.size() - 1; i++) {

            List<String> data = new ArrayList<String>(Arrays.asList(items.get(i).split(" ")));

            if (data.get(0).compareTo("OK\r\n") == 0) {
                System.out.print("Ok\n");
            }
            if (data.get(0).compareTo("CARD") == 0) {
                _cards.add(new Card(Integer.parseInt(data.get(1)), Integer.parseInt(data.get(2)), Integer.parseInt(data.get(3))));
            }
        }

        /* AFFICHAGE DES CARTES A DEPLACER */

        if (_cards.size() == 8) {
            System.out.print("Vos cartes sont :\n");
            for (int i = 0; i < 8; i++) {
                System.out.print(_cards.get(i).getNumber() + " de ");
                if (_cards.get(i).getColor() == 0) {
                    System.out.print("pique");
                } else if (_cards.get(i).getColor() == 1) {
                    System.out.print("trefle");
                } else if (_cards.get(i).getColor() == 2) {
                    System.out.print("carreau");
                } else if (_cards.get(i).getColor() == 3) {
                    System.out.print("coeur");
                }
                System.out.print(", valeur de " + _cards.get(i).getValue() + "\n");
            }
        }
    }
}
