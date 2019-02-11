package jp.co.gomaariiii.example.rabbitmq;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import io.vertx.core.Vertx;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import jp.co.gomaariiii.example.rabbitmq.RabbitmqVerticle;

@ExtendWith(VertxExtension.class)
public class TestRabbitmqVerticle {

  /** キュー名 */
  private static final String QUEUE_NAME = "queue";

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new RabbitmqVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  @DisplayName("Should start a Web Server on port 8080")
  @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
  void start_http_server(Vertx vertx, VertxTestContext testContext) throws Throwable {
    vertx.createHttpClient().getNow(8080, "localhost", "/", response -> testContext.verify(() -> {
      assertTrue(response.statusCode() == 200);
      response.handler(body -> {
        assertEquals("helloworld", body.toString());

        try {
          Receive(testContext);
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
    }));
  }

  private void Receive(VertxTestContext testContext) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    factory.setPort(5672);
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();

    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

    DefaultConsumer defaultConsumer = new DefaultConsumer(channel) {
      @Override
      public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
          throws IOException {
        String message = new String(body, "UTF-8");
        System.out.println(" [x] Received '" + message + "'");
      }
    };

    channel.basicConsume(QUEUE_NAME, false, defaultConsumer);
  }
}
