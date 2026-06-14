package com.example.inventory_management_system.service.impl;

import com.example.inventory_management_system.exception.BadRequestException;
import com.example.inventory_management_system.exception.ResourceNotFoundException;
import com.example.inventory_management_system.models.dto.request.SaleRequestDTO;
import com.example.inventory_management_system.models.dto.response.SaleResponseDTO;
import com.example.inventory_management_system.models.dto.response.SalesReportResponseDTO;
import com.example.inventory_management_system.models.entity.Product;
import com.example.inventory_management_system.models.entity.SaleItem;
import com.example.inventory_management_system.models.entity.SaleTransaction;
import com.example.inventory_management_system.models.entity.User;
import com.example.inventory_management_system.repository.ProductRepository;
import com.example.inventory_management_system.repository.SaleTransactionRepository;
import com.example.inventory_management_system.repository.UserRepository;
import com.example.inventory_management_system.service.SaleService;
import com.example.inventory_management_system.service.StockAlertService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {
    private final SaleTransactionRepository saleTransactionRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final StockAlertService stockAlertService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public SaleResponseDTO create(SaleRequestDTO request, String createdByEmail) {
        User user = userRepository.findByEmail(createdByEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        SaleTransaction transaction = SaleTransaction.builder()
                .invoiceNumber(generateInvoiceNumber())
                .createdBy(user)
                .customerName(request.getCustomerName())
                .customerEmail(request.getCustomerEmail())
                .taxPercent(request.getTaxPercent())
                .notes(request.getNotes())
                .build();

        List<SaleItem> items = request.getItems().stream().map(itemReq -> {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", itemReq.getProductId()));
            if (product.getQuantityInStock() < itemReq.getQuantity()) {
                throw new BadRequestException("Insufficient stock for product: " + product.getName()
                        + ". Available: " + product.getQuantityInStock()
                        + ", Requested: " + itemReq.getQuantity());
            }
            product.setQuantityInStock(product.getQuantityInStock() - itemReq.getQuantity());
            productRepository.save(product);
            stockAlertService.checkAndCreateAlerts(product);
            BigDecimal totalPrice = itemReq.getUnitPrice()
                    .multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            return SaleItem.builder()
                    .saleTransaction(transaction)
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .unitPrice(itemReq.getUnitPrice())
                    .totalPrice(totalPrice)
                    .build();
        }).toList();

        transaction.setItems(items);

        BigDecimal subtotal = items.stream()
                .map(SaleItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal taxAmount = subtotal
                .multiply(request.getTaxPercent())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        transaction.setSubtotal(subtotal);
        transaction.setTaxAmount(taxAmount);
        transaction.setTotalAmount(subtotal.add(taxAmount));

        return toResponseDTO(saleTransactionRepository.save(transaction));
    }

    @Override
    public SaleResponseDTO findById(Long id) {
        return toResponseDTO(findSale(id));
    }

    @Override
    public SaleResponseDTO findByInvoiceNumber(String invoiceNumber) {
        SaleTransaction tx = saleTransactionRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + invoiceNumber));
        return toResponseDTO(tx);
    }

    @Override
    public Page<SaleResponseDTO> findAll(Pageable pageable) {
        return saleTransactionRepository.findAll(pageable).map(this::toResponseDTO);
    }

    @Override
    public Page<SaleResponseDTO> findByDateRange(LocalDate from, LocalDate to, Pageable pageable) {
        return saleTransactionRepository.findByCreatedAtBetween(
                from.atStartOfDay(), to.atTime(23, 59, 59), pageable
        ).map(this::toResponseDTO);
    }

    @Override
    public SalesReportResponseDTO generateReport(LocalDate from, LocalDate to) {
        LocalDateTime fromDt = from.atStartOfDay();
        LocalDateTime toDt = to.atTime(23, 59, 59);

        BigDecimal totalRevenue = saleTransactionRepository.sumTotalRevenue(fromDt, toDt);
        Long totalTransactions = saleTransactionRepository.countTransactions(fromDt, toDt);

        List<Object[]> topRaw = saleTransactionRepository.findTopProducts(
                fromDt, toDt, PageRequest.of(0, 10));

        List<SalesReportResponseDTO.TopProductResponseDTO> topProducts = topRaw.stream().map(row ->
                SalesReportResponseDTO.TopProductResponseDTO.builder()
                        .productId(((Number) row[0]).longValue())
                        .productName((String) row[1])
                        .sku((String) row[2])
                        .totalQuantitySold(((Number) row[3]).intValue())
                        .totalRevenue((BigDecimal) row[4])
                        .build()
        ).toList();

        return SalesReportResponseDTO.builder()
                .reportPeriod(from.format(DateTimeFormatter.ISO_DATE) + " to " + to.format(DateTimeFormatter.ISO_DATE))
                .totalTransactions(totalTransactions)
                .totalRevenue(totalRevenue)
                .totalTax(BigDecimal.ZERO)
                .netRevenue(totalRevenue)
                .totalItemsSold(topProducts.stream()
                        .mapToInt(SalesReportResponseDTO.TopProductResponseDTO::getTotalQuantitySold).sum())
                .topProducts(topProducts)
                .dailySummary(List.of())
                .build();
    }

    private SaleTransaction findSale(Long id) {
        return saleTransactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SaleTransaction", id));
    }

    private String generateInvoiceNumber() {
        String prefix = "INV-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";
        String suffix = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return prefix + suffix;
    }

    private SaleResponseDTO toResponseDTO(SaleTransaction tx) {
        SaleResponseDTO dto = new SaleResponseDTO();
        dto.setId(tx.getId());
        dto.setInvoiceNumber(tx.getInvoiceNumber());
        dto.setCustomerName(tx.getCustomerName());
        dto.setCustomerEmail(tx.getCustomerEmail());
        dto.setCreatedByName(tx.getCreatedBy() != null ? tx.getCreatedBy().getFullName() : null);
        dto.setSubtotal(tx.getSubtotal());
        dto.setTaxPercent(tx.getTaxPercent());
        dto.setTaxAmount(tx.getTaxAmount());
        dto.setTotalAmount(tx.getTotalAmount());
        dto.setNotes(tx.getNotes());
        dto.setCreatedAt(tx.getCreatedAt());

        List<SaleResponseDTO.SaleItemResponseDTO> items = tx.getItems().stream().map(item -> {
            SaleResponseDTO.SaleItemResponseDTO ir = new SaleResponseDTO.SaleItemResponseDTO();
            ir.setId(item.getId());
            ir.setProductId(item.getProduct().getId());
            ir.setProductName(item.getProduct().getName());
            ir.setProductSku(item.getProduct().getSku());
            ir.setQuantity(item.getQuantity());
            ir.setUnitPrice(item.getUnitPrice());
            ir.setTotalPrice(item.getTotalPrice());
            return ir;
        }).toList();
        dto.setItems(items);
        return dto;
    }
}
