package dev.saberlabs.springcoffee.websocket;

import dev.saberlabs.springcoffee.facade.CoffeeShopFacade;
import dev.saberlabs.springcoffee.model.ChatSender;
import dev.saberlabs.springcoffee.model.CoffeeOrder;
import dev.saberlabs.springcoffee.model.CoffeeType;
import dev.saberlabs.springcoffee.model.Extra;
import dev.saberlabs.springcoffee.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.JacksonJsonMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Exercises the real STOMP broker end to end over an actual WebSocket connection, the same way
 * the previous raw-handler test did, just through {@code StompSession} instead of a bare
 * {@code java.net.http.WebSocket}.
 */
class ChatStompControllerTest extends AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private CoffeeShopFacade coffeeShopFacade;

    private StompSession connect(long orderId, List<ChatSocketPayload> received) throws Exception {
        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new JacksonJsonMessageConverter());

        CompletableFuture<StompSession> connected = new CompletableFuture<>();
        stompClient.connectAsync("ws://localhost:" + port + "/ws/chat", new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                session.subscribe("/topic/chat/" + orderId, new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return ChatSocketPayload.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        received.add((ChatSocketPayload) payload);
                    }
                });
                connected.complete(session);
            }
        });
        return connected.get(5, TimeUnit.SECONDS);
    }

    @Test
    void aMessageSentByOneSideReachesTheOther() throws Exception {
        CoffeeOrder order = coffeeShopFacade.placeOrder("CUST-1", CoffeeType.ESPRESSO, EnumSet.noneOf(Extra.class));

        List<ChatSocketPayload> customerSeen = new CopyOnWriteArrayList<>();
        List<ChatSocketPayload> baristaSeen = new CopyOnWriteArrayList<>();
        StompSession customerSession = connect(order.getId(), customerSeen);
        StompSession baristaSession = connect(order.getId(), baristaSeen);

        baristaSession.send("/app/chat/" + order.getId(),
                new ChatSocketPayload(ChatSender.BARISTA, "On it!", Instant.now()));

        await().atMost(Duration.ofSeconds(5))
                .until(() -> customerSeen.stream().anyMatch(message -> message.text().equals("On it!")));
        assertThat(baristaSeen).anyMatch(message -> message.text().equals("On it!"));

        customerSession.disconnect();
        baristaSession.disconnect();
    }

    @Test
    void fulfillingTheOrderBroadcastsASystemMessageToOpenSockets() throws Exception {
        CoffeeOrder order = coffeeShopFacade.placeOrder("CUST-1", CoffeeType.LATTE, EnumSet.noneOf(Extra.class));
        coffeeShopFacade.payOrder(order.getId());

        List<ChatSocketPayload> customerSeen = new CopyOnWriteArrayList<>();
        StompSession customerSession = connect(order.getId(), customerSeen);

        coffeeShopFacade.fulfillOrder(order.getId());

        await().atMost(Duration.ofSeconds(5))
                .until(() -> customerSeen.stream().anyMatch(message -> message.text().toLowerCase().contains("ready")));

        customerSession.disconnect();
    }
}
