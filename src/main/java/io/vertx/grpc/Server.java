package io.vertx.grpc;

import io.grpc.BindableService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.grpc.ConsumerServiceGrpc.ConsumerServiceVertxImplBase;
import java.util.UUID;

public class Server extends AbstractVerticle {

  @Override
  public void start() {

    BindableService bindableService = new ConsumerServiceVertxImplBase() {

      @Override
      public void connect(ClientRequest request, GrpcWriteStream<ClientResponse> response) {
        System.out.println("Connected " + request.getId());
        vertx.setPeriodic(1000, result -> response.write(ClientResponse.newBuilder()
            .setBody(UUID.randomUUID().toString()).build()));
      }
    };

    VertxServer rpcServer = VertxServerBuilder
        .forPort(vertx, 8080)
        .addService(bindableService)
        .build();

    rpcServer.start(result -> {
      if (result.succeeded()) {
        System.out.println("Server started at 8080");
      } else {
        System.out.println("Fail to start server " + result.cause().getMessage());
      }
    });

  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new Server());
  }

}