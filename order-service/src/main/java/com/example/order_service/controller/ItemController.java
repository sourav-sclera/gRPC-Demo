package com.example.order_service.controller;

import com.example.order_service.dto.ItemDTO;
import com.example.order_service.service.ItemService;
import org.springframework.web.bind.annotation.*;
import com.example.order_service.dto.UniversalResponseDTO;

import java.util.List;

@RestController
@RequestMapping("/api/item")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping("/add-item")
    public UniversalResponseDTO<ItemDTO> addItemToOrder(@RequestBody ItemDTO itemDTO) {
        return itemService.addItem(itemDTO);
    }

    @GetMapping("/get-all-items")
    public UniversalResponseDTO<List<ItemDTO>> getAllItems() {
        return itemService.getAllItems();
    }

    @GetMapping("/{id}/get-item")
    public UniversalResponseDTO<ItemDTO> getItemById(@PathVariable String id) {
        return itemService.getItemById(id);
    }

    @DeleteMapping("/delete-item")
    public UniversalResponseDTO<ItemDTO> deleteItem(@RequestBody List<String> ids) {
        return itemService.deleteItem(ids);
    }
}
