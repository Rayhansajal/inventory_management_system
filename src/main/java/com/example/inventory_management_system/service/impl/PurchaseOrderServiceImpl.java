package com.example.inventory_management_system.service.impl;

import com.example.inventory_management_system.exception.BadRequestException;
import com.example.inventory_management_system.exception.ResourceNotFoundException;
import com.example.inventory_management_system.models.dto.request.PurchaseOrderRequestDTO;
import com.example.inventory_management_system.models.dto.response.PurchaseOrderResponseDTO;
import com.example.inventory_management_system.models.entity.*;
import com.example.inventory_management_system.models.enums.OrderStatus;
import com.example.inventory_management_system.repository.ProductRepository;
import com.example.inventory_management_system.repository.PurchaseOrderRepository;
import com.example.inventory_management_system.repository.SupplierRepository;
import com.example.inventory_management_system.repository.UserRepository;
import com.example.inventory_management_system.service.PurchaseOrderService;
import com.example.inventory_management_system.service.StockAlertService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl implements PurchaseOrderService {
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final StockAlertService stockAlertService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public PurchaseOrderResponseDTO create(PurchaseOrderRequestDTO request, String createdByEmail) {
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", request.getSupplierId()));
        User user = userRepository.findByEmail(createdByEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        PurchaseOrder order = PurchaseOrder.builder()
                .orderNumber(generateOrderNumber())
                .supplier(supplier)
                .createdBy(user)
                .status(OrderStatus.PENDING)
                .notes(request.getNotes())
                .expectedDeliveryDate(request.getExpectedDeliveryDate())
                .build();

        List<PurchaseOrderItem> items = request.getItems().stream().map(itemReq -> {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", itemReq.getProductId()));
            BigDecimal total = itemReq.getUnitPrice()
                    .multiply(BigDecimal.valueOf(itemReq.getOrderedQuantity()));
            return PurchaseOrderItem.builder()
                    .purchaseOrder(order)
                    .product(product)
                    .orderedQuantity(itemReq.getOrderedQuantity())
                    .receivedQuantity(0)
                    .unitPrice(itemReq.getUnitPrice())
                    .totalPrice(total)
                    .build();
        }).toList();

        order.setItems(items);
        order.setTotalAmount(items.stream()
                .map(PurchaseOrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        return toResponseDTO(purchaseOrderRepository.save(order));
    }

    @Override
    public PurchaseOrderResponseDTO findById(Long id) {
        return toResponseDTO(findOrder(id));
    }

    @Override
    public PurchaseOrderResponseDTO findByOrderNumber(String orderNumber) {
        return toResponseDTO(purchaseOrderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderNumber)));
    }

    @Override
    public Page<PurchaseOrderResponseDTO> findAll(Pageable pageable) {
        return purchaseOrderRepository.findAll(pageable).map(this::toResponseDTO);
    }

    @Override
    public Page<PurchaseOrderResponseDTO> findByStatus(OrderStatus status, Pageable pageable) {
        return purchaseOrderRepository.findByStatus(status, pageable).map(this::toResponseDTO);
    }

    @Override
    @Transactional
    public PurchaseOrderResponseDTO updateStatus(Long id, OrderStatus status) {
        PurchaseOrder order = findOrder(id);
        if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.RECEIVED) {
            throw new BadRequestException("Cannot update a " + order.getStatus() + " order");
        }
        order.setStatus(status);
        return toResponseDTO(purchaseOrderRepository.save(order));
    }

    @Override
    @Transactional
    public PurchaseOrderResponseDTO receiveOrder(Long id) {
        PurchaseOrder order = findOrder(id);
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("Cannot receive a cancelled order");
        }
        order.getItems().forEach(item -> {
            Product product = item.getProduct();
            product.setQuantityInStock(product.getQuantityInStock() + item.getOrderedQuantity());
            item.setReceivedQuantity(item.getOrderedQuantity());
            productRepository.save(product);
            stockAlertService.checkAndCreateAlerts(product);
        });
        order.setStatus(OrderStatus.RECEIVED);
        order.setActualDeliveryDate(LocalDate.now());
        return toResponseDTO(purchaseOrderRepository.save(order));
    }

    @Override
    @Transactional
    public void cancel(Long id) {
        PurchaseOrder order = findOrder(id);
        if (order.getStatus() == OrderStatus.RECEIVED) {
            throw new BadRequestException("Cannot cancel a received order");
        }
        order.setStatus(OrderStatus.CANCELLED);
        purchaseOrderRepository.save(order);
    }

    private PurchaseOrder findOrder(Long id) {
        return purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", id));
    }

    private String generateOrderNumber() {
        String prefix = "PO-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";
        String suffix = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return prefix + suffix;
    }

    private PurchaseOrderResponseDTO toResponseDTO(PurchaseOrder order) {
        PurchaseOrderResponseDTO dto = new PurchaseOrderResponseDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setStatus(order.getStatus());
        dto.setSupplierId(order.getSupplier().getId());
        dto.setSupplierName(order.getSupplier().getName());
        dto.setCreatedByName(order.getCreatedBy() != null ? order.getCreatedBy().getFullName() : null);
        dto.setTotalAmount(order.getTotalAmount());
        dto.setNotes(order.getNotes());
        dto.setExpectedDeliveryDate(order.getExpectedDeliveryDate());
        dto.setActualDeliveryDate(order.getActualDeliveryDate());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());

        List<PurchaseOrderResponseDTO.PurchaseOrderItemResponseDTO> items = order.getItems().stream().map(item -> {
            PurchaseOrderResponseDTO.PurchaseOrderItemResponseDTO ir = new PurchaseOrderResponseDTO.PurchaseOrderItemResponseDTO();
            ir.setId(item.getId());
            ir.setProductId(item.getProduct().getId());
            ir.setProductName(item.getProduct().getName());
            ir.setProductSku(item.getProduct().getSku());
            ir.setOrderedQuantity(item.getOrderedQuantity());
            ir.setReceivedQuantity(item.getReceivedQuantity());
            ir.setUnitPrice(item.getUnitPrice());
            ir.setTotalPrice(item.getTotalPrice());
            return ir;
        }).toList();
        dto.setItems(items);
        return dto;
    }
}
