package fr.ybonnel.simpleweb4j.handlers.websocket;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Implementation of WebSocketListener with lambdas.
 * @param <I> type of input message (client -&gt; server).
 * @param <O> type of output message (server -&gt; client).
 */
class WebSocketListennerWithLambda<I, O> implements WebSocketListener<I, O> {


    /**
     * Type of input (client -&gt; server).
     */
    private final Class<I> inputType;
    /**
     * onClose listener.
     */
    private final BiConsumer<WebSocketSession<O>, CloseCause> onClose;
    /**
     * onError listener.
     */
    private final BiConsumer<WebSocketSession<O>, Throwable> onError;
    /**
     * onConnect listener.
     */
    private final Consumer<WebSocketSession<O>> onConnect;
    /**
     * onMessage listener.
     */
    private final BiConsumer<WebSocketSession<O>, I> onMessage;

    /**
     * Current session.
     */
    private WebSocketSession<O> currentSession;

    /**
     * Constructor.
     * @param inputType Type of input (client -&gt; server).
     * @param onClose onClose listener.
     * @param onError onError listener.
     * @param onConnect onConnect listener.
     * @param onMessage onMessage listener.
     */
    WebSocketListennerWithLambda(Class<I> inputType,
            BiConsumer<WebSocketSession<O>, CloseCause> onClose,
            BiConsumer<WebSocketSession<O>, Throwable> onError,
            Consumer<WebSocketSession<O>> onConnect,
            BiConsumer<WebSocketSession<O>, I> onMessage) {
        this.inputType = inputType;
        this.onClose = onClose;
        this.onError = onError;
        this.onConnect = onConnect;
        this.onMessage = onMessage;
    }

    /**
     * Method call on close event.
     *
     * @param statusCode status code.
     * @param reason     reason of close.
     */
    @Override
    public void onClose(int statusCode, String reason) {
        if (onClose != null) {
            onClose.accept(currentSession, new CloseCause(statusCode, reason));
        }
    }

    /**
     * Method call on error event.
     *
     * @param t exception of the error.
     */
    @Override
    public void onError(Throwable t) {
        if (onError != null) {
            onError.accept(currentSession, t);
        }
    }

    /**
     * Method call on connect event.
     *
     * @param session the WebSocket session.
     */
    @Override
    public void onConnect(WebSocketSession<O> session) {
        currentSession = session;
        if (onConnect != null) {
            onConnect.accept(session);
        }
    }

    /**
     * Method call on message event.
     *
     * @param message message received.
     */
    @Override
    public void onMessage(I message) {
        if (onMessage != null) {
            onMessage.accept(currentSession, message);
        }
    }

    /**
     * @return type of input message (client -&gt; server).
     */
    @Override
    public Class<I> getInputType() {
        return inputType;
    }
}
