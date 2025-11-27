package com.example.inventory_service.controller;

import com.example.inventory_service.Service.OrderService;
import com.example.inventory_service.dto.OrderDTO;
import com.example.inventory_service.dto.UniversalResponseDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create-order")
    public UniversalResponseDTO<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO) {
        return orderService.createOrder(orderDTO);
    }

    @GetMapping("/get-all-orders")
    public UniversalResponseDTO<List<OrderDTO>> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}/get-order")
    public UniversalResponseDTO<OrderDTO> getOrderById(@PathVariable String id) {
        return orderService.getOrderById(id);
    }

    @DeleteMapping("/delete-order")
    public UniversalResponseDTO<OrderDTO> deleteOrder(@RequestBody List<String> ids) {
        return orderService.deleteOrder(ids);
    }
}
