package com.example.order_service.impl;

import com.example.inventory.grpc.*;
import com.example.order_service.dto.OrderDTO;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@GrpcService
public class OrderServiceImpl extends OrderServiceGrpc.OrderServiceImplBase{
    private final Map<String, OrderDTO> orders = new ConcurrentHashMap<>();
    Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Override
    public void createOrder(UniversalRequest request, StreamObserver<UniversalResponse> responseObserver) {
        String id = UUID.randomUUID().toString();
        OrderDTO orderDTO = new OrderDTO(id, request.getPayload().getFieldsOrDefault("itemId", Value.newBuilder().setStringValue("").build()).getStringValue(),
                request.getPayload().getFieldsOrDefault("itemName", Value.newBuilder().setStringValue("").build()).getStringValue(),
                request.getPayload().getFieldsOrDefault("itemPrice", Value.newBuilder().setNumberValue(0).build()).getNumberValue(),
                request.getPayload().getFieldsOrDefault("quantity", Value.newBuilder().setNumberValue(0).build()).getNumberValue()
        );
        orders.put(orderDTO.getId(), orderDTO);
        Struct data = Struct.newBuilder()
                .putFields("id", Value.newBuilder().setStringValue(id).build())
                .putFields("itemId", Value.newBuilder().setStringValue(orderDTO.getItemId()).build())
                .putFields("itemName", Value.newBuilder().setStringValue(orderDTO.getItemName()).build())
                .putFields("itemPrice", Value.newBuilder().setNumberValue(orderDTO.getItemPrice()).build())
                .putFields("quantity", Value.newBuilder().setNumberValue(orderDTO.getQuantity()).build())
                .build();

        UniversalResponse response = UniversalResponse.newBuilder()
                .setStatus("SUCCESS")
                .setMessage("Order created successfully")
                .setData(data)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getAllOrders(UniversalRequest request, StreamObserver<UniversalListResponse> responseObserver) {
        List<OrderDTO> orderList = List.copyOf(orders.values());
        int totalCount = orderList.size();
        UniversalListResponse.Builder responseBuilder = UniversalListResponse.newBuilder()
                .setStatus("SUCCESS")
                .setMessage("Orders retrieved successfully")
                .setTotalCount(totalCount);

        for (OrderDTO order : orderList) {
            Struct orderStruct = Struct.newBuilder()
                    .putFields("id", Value.newBuilder().setStringValue(order.getId()).build())
                    .putFields("itemId", Value.newBuilder().setStringValue(order.getItemId()).build())
                    .putFields("itemName", Value.newBuilder().setStringValue(order.getItemName()).build())
                    .putFields("itemPrice", Value.newBuilder().setNumberValue(order.getItemPrice()).build())
                    .putFields("quantity", Value.newBuilder().setNumberValue(order.getQuantity()).build())
                    .build();
            responseBuilder.addItems(orderStruct);
        }
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getOrderById(UniversalRequest request, StreamObserver<UniversalResponse> responseObserver) {
        String id = request.getParamsOrDefault("id", "");
        OrderDTO order = orders.get(id);
        if (order == null) {
            responseObserver.onNext(UniversalResponse.newBuilder()
                    .setStatus("ERROR")
                    .setMessage("Order not found")
                    .build());
            responseObserver.onCompleted();
            return;
        }
        Struct data = Struct.newBuilder()
                .putFields("id", Value.newBuilder().setStringValue(id).build())
                .putFields("itemId", Value.newBuilder().setStringValue(order.getItemId()).build())
                .putFields("itemName", Value.newBuilder().setStringValue(order.getItemName()).build())
                .putFields("itemPrice", Value.newBuilder().setNumberValue(order.getItemPrice()).build())
                .putFields("quantity", Value.newBuilder().setNumberValue(order.getQuantity()).build())
                .build();

        UniversalResponse response = UniversalResponse.newBuilder()
                .setStatus("SUCCESS")
                .setMessage("Order retrieved successfully")
                .setData(data)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteOrder(UniversalRequest request, StreamObserver<UniversalResponse> responseObserver) {
        String idStr = request.getParamsOrDefault("id", "");
        List<String> ids = Arrays.asList(idStr.split(","));
        ids.forEach(orders::remove);

        UniversalResponse response = UniversalResponse.newBuilder()
                .setStatus("SUCCESS")
                .setMessage("Order deleted successfully")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
