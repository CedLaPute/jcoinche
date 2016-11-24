import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * Created by héhéhéhéhéhéhéhé on 24/11/2016.
 */
public class Command {

    private ArrayList<Card> _cards = new ArrayList<Card>();
    private FutureTask<String> _future;
    private ExecutorService _executor;
    public String _login;

    public void startReadingThread() throws Exception {
        CallableReader readin = new CallableReader();
        this._future = new FutureTask<String>(readin);
        this._executor = Executors.newFixedThreadPool(1);
        this._executor.execute(this._future);
    }

    public void showDeck() {
        for (int i = 0; i < this._cards.size(); i++) {
            System.out.print("La carte n°" + (i + 1) + " est un");
            showCard(this._cards.get(i));
        }
    }

    public void showCard(Card c) {
        switch (c.getNumber()) {
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
        switch (c.getColor()) {
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
        System.out.print(" , avec une valeur de " + c.getValue());
        System.out.print("\n");
    }

    public void ready(ChannelHandlerContext context) throws Exception {
        showDeck();
        System.out.print("Tapez READY si vous êtes prêts à jouer\n");
        try {
            while (true) {

                startReadingThread();
                String str;

                if (!this._future.isDone()) {
                    str = this._future.get();
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

    public void bet(ChannelHandlerContext context) throws Exception {
        startReadingThread();
        System.out.print("Quelle pari choisissez-vous ?\n" + "Il vous suffit de rentrer un nombre entier > 0\n");

        try {
            String str;

            while (true) {
                if (!this._future.isDone()) {
                    str = this._future.get();
                    if (StringUtils.isAlphanumeric(str)) {
                        int k;
                        k = Integer.parseInt(str);
                        if (k > 0) {
                            context.writeAndFlush(new Serializer().sendBet(k, this._login));
                            this._executor.shutdown();
                            return;
                        }
                        else {
                            context.writeAndFlush(new Serializer().sendBet(0, this._login));
                            this._executor.shutdown();
                            return;
                        }
                    }
                }
            }
        } finally{}
    }

    public void playCard(ChannelHandlerContext context) throws Exception {
        showDeck();
        startReadingThread();
        System.out.print("Quelle carte choisissez-vous ?\nIl vous suffit de rentrer un nombre entre 1 et " + this._cards.size() + ".\n");
        try {
            String str;

            while (true) {
                if (!this._future.isDone()) {
                    str = this._future.get();
                    if (StringUtils.isAlphanumeric(str)) {
                        int k;
                        k = Integer.parseInt(str);
                        if (k >= 1 && k <= this._cards.size()) {
                            System.out.print("Vous avez jouer la " + str + "° carte.\n");
                            context.writeAndFlush(new Serializer().sendPlayerCard(this._login, this._cards.get(k - 1)));
                            this._cards.remove(k - 1);
                            showDeck();
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
                if (_cards.size() == 8) {
                    ready(context);
                }
            }
            if (data.get(0).compareTo("BET") == 0) {
                bet(context);
            }
            if (data.get(0).compareTo("PLAY") == 0)
            {
                playCard(context);
            }
            if (data.get(0).compareTo("PLAYCARD") == 0) {
                System.out.print(data.size());
                if (data.size() == 5) {
                    System.out.print("-" + data.get(1) + "- a joué un");
                    showCard(new Card(Integer.parseInt(data.get(2)), Integer.parseInt(data.get(3)), Integer.parseInt(data.get(4))));
                }
            }
        }
    }
}
