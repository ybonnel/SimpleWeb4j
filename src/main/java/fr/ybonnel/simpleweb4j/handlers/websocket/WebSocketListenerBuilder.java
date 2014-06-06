package fr.ybonnel.simpleweb4j.handlers.websocket;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Builder fo WebSocketListener.
 * @param <I> type of input message (client -&gt; server).
 * @param <O> type of output message (server -&gt; client).
 */
public class WebSocketListenerBuilder<I, O> {

    /**
     * Constructor.
     * @param inputType type of input message (client -&gt; server).
     */
    WebSocketListenerBuilder(Class<I> inputType) {
        this.inputType = inputType;
    }

    /**
     * Add onClose listener.
     * @param onClose onClose listenner.
     * @return builder.
     */
    public WebSocketListenerBuilder<I, O> onClose(BiConsumer<WebSocketSession<O>, CloseCause> onClose) {
        this.onCloseHandler = onClose;
        return this;
    }

    /**
     * Add onError listener.
     * @param onError onClose listenner.
     * @return builder.
     */
    public WebSocketListenerBuilder<I, O> onError(BiConsumer<WebSocketSession<O>, Throwable> onError) {
        this.onErrorHandler = onError;
        return this;
    }

    /**
     * Add onConnect listener.
     * @param onConnect onClose listenner.
     * @return builder.
     */
    public WebSocketListenerBuilder<I, O> onConnect(Consumer<WebSocketSession<O>> onConnect) {
        this.onConnectHandler = onConnect;
        return this;
    }

    /**
     * Add onMessage listener.
     * @param onMessage onClose listenner.
     * @return builder.
     */
    public WebSocketListenerBuilder<I, O> onMessage(BiConsumer<WebSocketSession<O>, I> onMessage) {
        this.onMessageHandler = onMessage;
        return this;
    }

    /**
     * Builder the WebSocketListener.
     * @return the listener.
     */
    public WebSocketListener<I, O> build() {
        return new WebSocketListennerWithLambda<>(inputType, onCloseHandler, onErrorHandler, onConnectHandler,
                onMessageHandler);
    }

    /**
     * Type of input (client -&gt; server).
     */
    private final Class<I> inputType;
    /**
     * onClose listener.
     */
    private BiConsumer<WebSocketSession<O>, CloseCause> onCloseHandler = null;
    /**
     * onError listener.
     */
    private BiConsumer<WebSocketSession<O>, Throwable> onErrorHandler = null;
    /**
     * onConnect listener.
     */
    private Consumer<WebSocketSession<O>> onConnectHandler = null;
    /**
     * onMessage listener.
     */
    private BiConsumer<WebSocketSession<O>, I> onMessageHandler = null;
}
