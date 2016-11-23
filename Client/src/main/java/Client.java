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

import org.apache.commons.lang3.StringUtils;

/**
 * Created by héhéhéhéhéhéhéhé on 17/11/2016.
 */
public class Client extends ChannelInboundHandlerAdapter {

    private ArrayList<Card> _cards = new ArrayList<Card>();
    private FutureTask<String> _future;
    private ExecutorService _executor;
    private String _login;

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
                    this._login = str;
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

    public void playCard(ChannelHandlerContext context) throws Exception {
        for (int i = 0; i < this._cards.size(); i++) {
            System.out.print("La carte n°" + (i + 1) + " est un");
            switch (this._cards.get(i).getNumber()) {
                case 7:
                    System.out.print(" 7 de");
                    break;
                case 8:
                    System.out.print(" 8 de");
                    break;
                case 9:
                    System.out.print(" 9 de");
                    break;
                case 10:
                    System.out.print(" 10 de");
                    break;
                case 11:
                    System.out.print(" Valet de");
                    break;
                case 12:
                    System.out.print("e Dame de");
                    break;
                case 13:
                    System.out.print(" Roi de");
                    break;
                case 14:
                    System.out.print(" As de");
                    break;
                default:
                    System.out.print(" --unknow-- de");
            }
            switch (this._cards.get(i).getColor()) {
                case 0:
                    System.out.print(" Pique");
                    break;
                case 1:
                    System.out.print(" Trefle");
                    break;
                case 2:
                    System.out.print(" Carreau");
                    break;
                case 3:
                    System.out.print(" Coeur");
                    break;
                default:
                    System.out.print(" --unknow--");
            }
        }
        startReadingThread();
        System.out.print("Quelle carte choisissez-vous ?\nIl vous suffit de rentrer un nombre entre 0 et " + this._cards.size() + ".\n");
        //usage de comment donné la carte
        try {
            String str;

            while (true) {
                if (!this._future.isDone()) {
                    str = this._future.get();
                    if (!StringUtils.isAlphanumeric(str)) {
                        int k;
                        k = Integer.parseInt(str);
                        if (!(k < 0 && k > this._cards.size())) {
                            System.out.print("Vous avez jouer la " + str + "° carte.\n");
                        }
                        this._executor.shutdown();
                        break;
                    }
                }
            }
        } finally{}
    }

    public void analyseCommand(ChannelHandlerContext context, String s) throws Exception {
        List<String> items = new ArrayList<String>(Arrays.asList(s.split("\r\n")));

        for (int i = 0; i < items.size() - 1; i++) {

            List<String> data = new ArrayList<String>(Arrays.asList(items.get(i).split(" ")));



            if (data.get(0).compareTo("OK") == 0) {
                System.out.print("Ok\n");
            }
            if (data.get(0).compareTo("DECK") == 0) {
                for (int dataIndex = 2; dataIndex < data.size(); dataIndex = dataIndex + 3) {
                    _cards.add(new Card(Integer.parseInt(data.get(dataIndex)), Integer.parseInt(data.get(dataIndex + 1)), Integer.parseInt(data.get(dataIndex + 2))));
                }
            }
            if (data.get(0).compareTo("PLAY") == 0)
            {
                playCard(context);
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
                while (true) {

                    startReadingThread();
                    String str;

                    if (!this._future.isDone()) {
                        str = this._future.get();
                        System.out.print("Readed : " + str + "\n");
                        if (str.compareTo("READY") == 0) {
                            context.writeAndFlush(new Serializer().sendReady(this._login));
                            this._executor.shutdown();
                            break;
                        }
                        else {
                            System.out.print("Tapez READY si vous êtes prêts à jouer\n");
                            this._executor.shutdown();
                        }
                    }
                }
            } finally {}
        }
    }
}
