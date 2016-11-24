import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * Created by héhéhéhéhéhéhéhé on 17/11/2016.
 */
public class Client extends ChannelInboundHandlerAdapter {

    private FutureTask<String> _future;
    private ExecutorService _executor;
    private Command _command = new Command();

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
                    this._command._login = str;
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
        this._command.analyseCommand(context, s);
    }
}
