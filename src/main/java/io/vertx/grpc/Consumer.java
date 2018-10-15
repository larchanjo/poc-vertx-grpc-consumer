package io.vertx.grpc;

import io.grpc.ManagedChannel;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.grpc.ConsumerServiceGrpc.ConsumerServiceVertxStub;
import java.util.UUID;

public class Consumer extends AbstractVerticle {

  @Override
  public void start() {

    ManagedChannel channel = VertxChannelBuilder
        .forAddress(vertx, "localhost", 8080)
        .usePlaintext(true)
        .build();

    ConsumerServiceVertxStub stub = ConsumerServiceGrpc.newVertxStub(channel);

    ClientRequest clientRequest = ClientRequest.newBuilder()
        .setId(UUID.randomUUID().toString())
        .build();

    stub.connect(clientRequest, stream -> {
      stream.handler(clientResponse -> {
        System.out.println(clientResponse);
      }).endHandler(v -> {
        System.out.println("End streaming");
      });
    });
  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new Consumer());
  }

}