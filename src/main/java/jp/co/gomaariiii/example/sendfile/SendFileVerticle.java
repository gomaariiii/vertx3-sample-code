package jp.co.gomaariiii.example.sendfile;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerResponse;

public class SendFileVerticle extends AbstractVerticle {

  @Override
  public void start() throws Exception {

    vertx.createHttpServer().requestHandler(req -> {

      System.out.println("start SendFile.");
      HttpServerResponse response = req.response();
      String requestPath = req.path();
      System.out.println("path=" + requestPath);
      if (requestPath == null) {
        requestPath = "/";
      }
      
      String filepath = "";
      switch (requestPath) {
      case "/web/index":
        filepath = "web/html/index.html";
        break;
      case "/css/index.css":
        filepath = "web/css/index.css";
        break;
      default:
        response.setStatusCode(500).setStatusMessage("invalid URL").end();
        return;
      }
      response.sendFile(filepath);
    }).listen(8080);
  }
}
