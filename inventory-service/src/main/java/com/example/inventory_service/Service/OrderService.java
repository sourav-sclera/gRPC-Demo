package com.example.inventory_service.Service;

import com.example.inventory.grpc.*;
import com.example.inventory_service.dto.OrderDTO;
import com.example.inventory_service.dto.UniversalResponseDTO;
import com.example.inventory_service.util.ProtoMapper;
import com.google.protobuf.Struct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderServiceGrpc.OrderServiceBlockingStub stub;

    public OrderService(OrderServiceGrpc.OrderServiceBlockingStub stub) {
        this.stub = stub;
    }

    public UniversalResponseDTO<OrderDTO> createOrder(OrderDTO orderDTO) {
        Struct payload = Struct.newBuilder()
                .putFields("itemId", com.google.protobuf.Value.newBuilder().setStringValue(orderDTO.getItemId()).build())
                .putFields("itemName", com.google.protobuf.Value.newBuilder().setStringValue(orderDTO.getItemName()).build())
                .putFields("itemPrice", com.google.protobuf.Value.newBuilder().setNumberValue(orderDTO.getItemPrice()).build())
                .putFields("quantity", com.google.protobuf.Value.newBuilder().setNumberValue(orderDTO.getQuantity()).build())
                .build();

        UniversalRequest request = UniversalRequest.newBuilder()
                .setPayload(payload)
                .build();

        UniversalResponse response = stub.createOrder(request);

        OrderDTO orderData = ProtoMapper.convertStruct(
                response.getData(),
                OrderDTO.class
        );

        logger.info("Response: {}", response);
        return UniversalResponseDTO.<OrderDTO>builder()
                .status(response.getStatus())
                .message(response.getMessage())
                .data(orderData)
                .build();
    }

    public UniversalResponseDTO<List<OrderDTO>> getAllOrders() {
        UniversalRequest request = UniversalRequest.newBuilder().build();

        UniversalListResponse response = stub.getAllOrders(request);
        List<OrderDTO> orders = ProtoMapper.convertStructList(
                response.getItemsList(),
                OrderDTO.class
        );

        logger.info("Response: {}", response);

        return UniversalResponseDTO.<List<OrderDTO>>builder()
                .status(response.getStatus())
                .message(response.getMessage())
                .data(orders)
                .build();
    }


    public UniversalResponseDTO<OrderDTO> getOrderById(String id) {

        UniversalRequest request = UniversalRequest.newBuilder()
                .putParams("id", id)
                .build();

        UniversalResponse response = stub.getOrderById(request);

        OrderDTO orderData = ProtoMapper.convertStruct(
                response.getData(),
                OrderDTO.class
        );

        logger.info("Response: {}", response);

        return UniversalResponseDTO.<OrderDTO>builder()
                .status(response.getStatus())
                .message(response.getMessage())
                .data(orderData)
                .build();
    }

    public UniversalResponseDTO<OrderDTO> deleteOrder(List<String> ids) {
        UniversalRequest request = UniversalRequest.newBuilder()
                .putParams("id", String.join(",", ids))
                .build();

        UniversalResponse response = stub.deleteOrder(request);

        OrderDTO orderData = ProtoMapper.convertStruct(
                response.getData(),
                OrderDTO.class
        );

        logger.info("Response: {}", response);

        return UniversalResponseDTO.<OrderDTO>builder()
                .status(response.getStatus())
                .message(response.getMessage())
                .data(orderData)
                .build();
    }
}
