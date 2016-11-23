import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * Created by héhéhéhéhéhéhéhé on 17/11/2016.
 */
public class Client extends ChannelInboundHandlerAdapter {

    private ArrayList<Card> _cards = new ArrayList<Card>();
    private FutureTask<String> _future;
    private ExecutorService _executor;

    public void startReadingThread() throws Exception {
        CallableReader readin = new CallableReader();
        this._future = new FutureTask<String>(readin);
        this._executor = Executors.newFixedThreadPool(1);
        this._executor.execute(this._future);
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object message) throws Exception {
        ByteBuf in = (ByteBuf) message;

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
        startReadingThread();
        System.out.print("Veuillez entrer votre login :\n");
        try {
            String str;

            while (true) {
                if (!this._future.isDone()) {
                    str = this._future.get();
                    context.writeAndFlush(new Serializer().sendLogin(str));
                    this._executor.shutdown();
                    break;
                }
            }
        } finally {}
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

    public void analyseCommand(ChannelHandlerContext context, String s) throws Exception {
        List<String> items = new ArrayList<String>(Arrays.asList(s.split("\r\n")));

        for (int i = 0; i < items.size() - 1; i++) {

            List<String> data = new ArrayList<String>(Arrays.asList(items.get(i).split(" ")));

            if (data.get(0).compareTo("OK") == 0) {
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
            System.out.print("Tapez READY si vous êtes prêts à jouer\n");
            try {
                String str;

                if (!this._future.isDone()) {
                    str = this._future.get();
                    System.out.print("Readed : " + str + "\n");
                    if (str.compareTo("READY") == 0) {
                        context.writeAndFlush(new Serializer().sendReady());
                    }
                    this._executor.shutdown();
                    startReadingThread();
                }
            } finally {}
        }
    }
}
