package com.example.order_service.service;

import com.example.inventory.grpc.UniversalListResponse;
import com.example.order_service.dto.ItemDTO;
import com.example.order_service.dto.UniversalResponseDTO;
import com.example.order_service.util.ProtoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.example.inventory.grpc.InventoryServiceGrpc;
import com.example.inventory.grpc.UniversalRequest;
import com.example.inventory.grpc.UniversalResponse;
import com.google.protobuf.Struct;

import java.util.List;

@Service
public class ItemService {
    
    private static final Logger logger = LoggerFactory.getLogger(ItemService.class);

    private final InventoryServiceGrpc.InventoryServiceBlockingStub stub;

    public ItemService(InventoryServiceGrpc.InventoryServiceBlockingStub stub) {
        this.stub = stub;
    }

    public UniversalResponseDTO<ItemDTO> addItem(ItemDTO itemDTO) {
        Struct payload = Struct.newBuilder()
                .putFields("name", com.google.protobuf.Value.newBuilder().setStringValue(itemDTO.getName()).build())
                .putFields("description", com.google.protobuf.Value.newBuilder().setStringValue(itemDTO.getDescription()).build())
                .putFields("price", com.google.protobuf.Value.newBuilder().setNumberValue(itemDTO.getPrice()).build())
                .putFields("imageUrl", com.google.protobuf.Value.newBuilder().setStringValue(itemDTO.getImageUrl()).build())
                .build();

        UniversalRequest request = UniversalRequest.newBuilder()
                .setPayload(payload)
                .build();

        UniversalResponse response = stub.createItem(request);

        ItemDTO itemData = ProtoMapper.convertStruct(
                response.getData(),
                ItemDTO.class
        );

        logger.info("Response: {}", response);

        return UniversalResponseDTO.<ItemDTO>builder()
                .status(response.getStatus())
                .message(response.getMessage())
                .data(itemData)
                .build();
    }

    public UniversalResponseDTO<List<ItemDTO>>  getAllItems() {
        UniversalRequest request = UniversalRequest.newBuilder().build();

        UniversalListResponse response = stub.getAllItems(request);
        List<ItemDTO> items = ProtoMapper.convertStructList(
                response.getItemsList(),
                ItemDTO.class
        );
        logger.info("Response: {}", response);
        return UniversalResponseDTO.<List<ItemDTO>>builder()
                .status(response.getStatus())
                .message(response.getMessage())
                .data(items)
                .build();
    }


    public UniversalResponseDTO<ItemDTO> getItemById(String id) {
        UniversalRequest request = UniversalRequest.newBuilder()
                .putParams("id", id)
                .build();

        UniversalResponse response = stub.getItemById(request);

        ItemDTO itemData = ProtoMapper.convertStruct(
                response.getData(),
                ItemDTO.class
        );

        logger.info("Response: {}", response);

        return UniversalResponseDTO.<ItemDTO>builder()
                .status(response.getStatus())
                .message(response.getMessage())
                .data(itemData)
                .build();
    }

    public UniversalResponseDTO<ItemDTO> deleteItem(List<String> ids) {
        UniversalRequest request = UniversalRequest.newBuilder()
                .putParams("id", String.join(",", ids))
                .build();

        UniversalResponse response = stub.deleteItem(request);

        ItemDTO itemData = ProtoMapper.convertStruct(
                response.getData(),
                ItemDTO.class
        );

        logger.info("Response: {}", response);

        return UniversalResponseDTO.<ItemDTO>builder()
                .status(response.getStatus())
                .message(response.getMessage())
                .data(itemData)
                .build();
    }
}
