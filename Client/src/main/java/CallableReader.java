import java.util.Scanner;
import java.util.concurrent.Callable;

/**
 * Created by héhéhéhéhéhéhéhé on 22/11/2016.
 */

// THREAD POUR LECTURE DE L'USER INPUT

public class CallableReader implements Callable<String> {

    private String readed = "";

    public CallableReader() {}

    public String call() throws Exception {
        Scanner scan = new Scanner(System.in);

        do {
            readed = scan.next();
        } while (readed.compareTo("") == 0);
        return this.readed;
    }
}
