package dev.saberlabs.springcoffee.websocket;

import dev.saberlabs.springcoffee.facade.CoffeeShopFacade;
import dev.saberlabs.springcoffee.model.CoffeeOrder;
import dev.saberlabs.springcoffee.model.CoffeeType;
import dev.saberlabs.springcoffee.model.Extra;
import dev.saberlabs.springcoffee.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.time.Duration;
import java.util.EnumSet;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

class ChatWebSocketHandlerTest extends AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private CoffeeShopFacade coffeeShopFacade;

    private WebSocket connect(long orderId, StringBuilder received) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        return client.newWebSocketBuilder()
                .buildAsync(URI.create("ws://localhost:" + port + "/ws/chat?orderId=" + orderId), new WebSocket.Listener() {
                    @Override
                    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
                        received.append(data);
                        webSocket.request(1);
                        return null;
                    }
                })
                .get(5, TimeUnit.SECONDS);
    }

    @Test
    void aMessageSentByOneSideReachesTheOther() throws Exception {
        CoffeeOrder order = coffeeShopFacade.placeOrder("CUST-1", CoffeeType.ESPRESSO, EnumSet.noneOf(Extra.class));

        StringBuilder customerSeen = new StringBuilder();
        StringBuilder baristaSeen = new StringBuilder();
        WebSocket customerSocket = connect(order.getId(), customerSeen);
        WebSocket baristaSocket = connect(order.getId(), baristaSeen);

        baristaSocket.sendText("{\"sender\":\"BARISTA\",\"text\":\"On it!\"}", true);

        await().atMost(Duration.ofSeconds(5)).until(() -> customerSeen.toString().contains("On it!"));
        assertThat(baristaSeen.toString()).contains("On it!");

        customerSocket.sendClose(WebSocket.NORMAL_CLOSURE, "done");
        baristaSocket.sendClose(WebSocket.NORMAL_CLOSURE, "done");
    }

    @Test
    void fulfillingTheOrderBroadcastsASystemMessageToOpenSockets() throws Exception {
        CoffeeOrder order = coffeeShopFacade.placeOrder("CUST-1", CoffeeType.LATTE, EnumSet.noneOf(Extra.class));
        coffeeShopFacade.payOrder(order.getId());

        StringBuilder customerSeen = new StringBuilder();
        WebSocket customerSocket = connect(order.getId(), customerSeen);

        coffeeShopFacade.fulfillOrder(order.getId());

        await().atMost(Duration.ofSeconds(5)).until(() -> customerSeen.toString().contains("ready"));

        customerSocket.sendClose(WebSocket.NORMAL_CLOSURE, "done");
    }
}
