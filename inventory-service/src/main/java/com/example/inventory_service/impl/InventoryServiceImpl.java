package com.example.inventory_service.impl;

import com.example.inventory.grpc.InventoryServiceGrpc;
import com.example.inventory.grpc.UniversalListResponse;
import com.example.inventory.grpc.UniversalRequest;
import com.example.inventory.grpc.UniversalResponse;
import com.example.inventory_service.dto.ItemDTO;
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
public class InventoryServiceImpl extends InventoryServiceGrpc.InventoryServiceImplBase {
    private final Map<String, ItemDTO> inventoryItems = new ConcurrentHashMap<>();
    Logger logger = LoggerFactory.getLogger(InventoryServiceImpl.class);

    @Override
    public void createItem(UniversalRequest request, StreamObserver<UniversalResponse> responseObserver) {

        String id = UUID.randomUUID().toString();
        ItemDTO item = new ItemDTO(id, request.getPayload().getFieldsOrDefault("name", Value.newBuilder().setStringValue("").build()).getStringValue(),
                request.getPayload().getFieldsOrDefault("description", Value.newBuilder().setStringValue("").build()).getStringValue(),
                request.getPayload().getFieldsOrDefault("price", Value.newBuilder().setNumberValue(0).build()).getNumberValue(),
                request.getPayload().getFieldsOrDefault("imageUrl", Value.newBuilder().setStringValue("").build()).getStringValue()
        );
        inventoryItems.put(id, item);
        Struct data = Struct.newBuilder()
                .putFields("id", Value.newBuilder().setStringValue(id).build())
                .putFields("name", Value.newBuilder().setStringValue(item.getName()).build())
                .putFields("description", Value.newBuilder().setStringValue(item.getDescription()).build())
                .putFields("price", Value.newBuilder().setNumberValue(item.getPrice()).build())
                .putFields("imageUrl", Value.newBuilder().setStringValue(item.getImageUrl()).build())
                .build();

        UniversalResponse response = UniversalResponse.newBuilder()
                .setStatus("SUCCESS")
                .setMessage("Item created successfully")
                .setData(data)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getAllItems(UniversalRequest request, StreamObserver<UniversalListResponse> responseObserver) {
        List<ItemDTO> itemList = List.copyOf(inventoryItems.values());
        int totalCount = itemList.size();
        UniversalListResponse.Builder responseBuilder = UniversalListResponse.newBuilder()
                .setStatus("SUCCESS")
                .setMessage("Items retrieved successfully")
                .setTotalCount(totalCount);

        for( ItemDTO item : itemList) {
            Struct itemStruct = Struct.newBuilder()
                    .putFields("id", Value.newBuilder().setStringValue(item.getId()).build())
                    .putFields("name", Value.newBuilder().setStringValue(item.getName()).build())
                    .putFields("description", Value.newBuilder().setStringValue(item.getDescription()).build())
                    .putFields("price", Value.newBuilder().setNumberValue(item.getPrice()).build())
                    .putFields("imageUrl", Value.newBuilder().setStringValue(item.getImageUrl()).build())
                    .build();

            responseBuilder.addItems(itemStruct);
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getItemById(UniversalRequest request, StreamObserver<UniversalResponse> responseObserver) {
        String id = request.getParamsOrDefault("id", "");
        ItemDTO item = inventoryItems.get(id);
        if (item == null) {
            responseObserver.onNext(UniversalResponse.newBuilder()
                    .setStatus("ERROR")
                    .setMessage("Item not found")
                    .build());
            responseObserver.onCompleted();
            return;
        }
        Struct data = Struct.newBuilder()
                .putFields("id", Value.newBuilder().setStringValue(id).build())
                .putFields("name", Value.newBuilder().setStringValue(item.getName()).build())
                .putFields("description", Value.newBuilder().setStringValue(item.getDescription()).build())
                .putFields("price", Value.newBuilder().setNumberValue(item.getPrice()).build())
                .putFields("imageUrl", Value.newBuilder().setStringValue(item.getImageUrl()).build())
                .build();

        UniversalResponse response = UniversalResponse.newBuilder()
                .setStatus("SUCCESS")
                .setMessage("Item retrieved successfully")
                .setData(data)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteItem(UniversalRequest request, StreamObserver<UniversalResponse> responseObserver) {
        String idStr = request.getParamsOrDefault("id", "");
        List<String> ids = Arrays.asList(idStr.split(","));
        ids.forEach(inventoryItems::remove);

        UniversalResponse response = UniversalResponse.newBuilder()
                .setStatus("SUCCESS")
                .setMessage("Item deleted successfully")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }
}
