import java.util.ArrayList;

/**
 * Created by héhéhéhéhéhéhéhé on 24/11/2016.
 */
public class Game {

    public boolean _distribute = false;
    public boolean _bet = false;
    public boolean _playing = false;
    public boolean _over;

    public void getGameInfo(ArrayList<Player> players) {
        this._over = true;
        if (this._playing == true) { // VERIFICATION SI IL Y A ENCORE DES CARTES A JOUER
            for (int i = 0; i < players.size(); i++) {
                if (players.get(i)._cards.size() > 0) {
                    this._over = false;
                }
            }
        }
        else {
            this._over = false;
        }
    }

    public int getPlayerIndexByName(ArrayList<Player> players, String login) {
        int i = 0;

        if (players.size() == 0) {
            return -1;
        }

        while (i < players.size()) {
            if (players.get(i)._login.compareTo(login) == 0) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public boolean allPlayersAreReady(ArrayList<Player> players) {

        if (players.size() < 4) {
            return false;
        }

        for (int i = 0; i < players.size(); i++) {
            if (players.get(i)._ready == false)
                return false;
        }
        return true;
    }

    public boolean haveAllPlayersBet(ArrayList<Player> players) {

        if (players.size() < 4) {
            return false;
        }

        for (int i = 0; i < players.size(); i++) {
            if (players.get(i)._bet == -1) {
                return false;
            }
        }
        return true;
    }

    public void removeCardFromPlayer(Player player, Card c) {
        int cardIndex;

        if ((cardIndex = player.getCardByIndex(c)) != -1) {
            player.removeCard(cardIndex);
        }
    }

    public int getPlayerTurnByIndex(ArrayList<Player> players) {
        int actualPlayerIndex = 0;

        if (players.size() == 4) {
            for (int i = 0; i < players.size(); i++) {
                if (players.get(i)._play == true) {
                    actualPlayerIndex = i;
                }
            }
        }
        if (actualPlayerIndex == players.size() - 1) {
            actualPlayerIndex = 0;
        } else {
            actualPlayerIndex++;
        }
        System.out.print("New playerTurn index : " + actualPlayerIndex);
        return actualPlayerIndex;
    }
}
