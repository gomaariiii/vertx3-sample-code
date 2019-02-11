package jp.co.gomaariiii.example.rabbitmq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;

public class RabbitmqVerticle extends AbstractVerticle {

  /** キュー名 */
  private static final String QUEUE_NAME = "queue";

  @Override
  public void start() throws Exception {
    HttpServer httpServer = vertx.createHttpServer();
    httpServer.requestHandler(request -> {
      HttpServerResponse response = request.response();
      send();
      response.putHeader("content-type", "text/html").end("helloworld");
    }).listen(8080);
  }

  private void send() {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    factory.setPort(5672);
    try (Connection connection = factory.newConnection();
        Channel channel = connection.createChannel()) {
      
      channel.queueDeclare(QUEUE_NAME, false, false, false, null);
      String message = "HelloWorld!!";
      channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
      System.out.println("send message=" + message);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (TimeoutException e) {
      e.printStackTrace();
    }
  }
}
