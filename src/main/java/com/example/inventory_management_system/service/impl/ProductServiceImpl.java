package com.example.inventory_management_system.service.impl;

import com.example.inventory_management_system.exception.BadRequestException;
import com.example.inventory_management_system.exception.ResourceNotFoundException;
import com.example.inventory_management_system.models.dto.request.ProductRequestDTO;
import com.example.inventory_management_system.models.dto.response.ProductResponseDTO;
import com.example.inventory_management_system.models.entity.Product;
import com.example.inventory_management_system.models.entity.Supplier;
import com.example.inventory_management_system.repository.ProductRepository;
import com.example.inventory_management_system.repository.SupplierRepository;
import com.example.inventory_management_system.service.ProductService;
import com.example.inventory_management_system.service.StockAlertService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final StockAlertService stockAlertService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public ProductResponseDTO create(ProductRequestDTO request) {
        if (productRepository.existsBySku(request.getSku())) {
            throw new BadRequestException("SKU already exists: " + request.getSku());
        }
        if (request.getBarcode() != null && productRepository.existsByBarcode(request.getBarcode())) {
            throw new BadRequestException("Barcode already exists: " + request.getBarcode());
        }
        Product product = modelMapper.map(request, Product.class);
        product.setId(null);
        if (request.getSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(request.getSupplierId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier", request.getSupplierId()));
            product.setSupplier(supplier);
        }
        Product saved = productRepository.save(product);
        stockAlertService.checkAndCreateAlerts(saved);
        return toResponseDTO(saved);
    }

    @Override
    @Transactional
    public ProductResponseDTO update(Long id, ProductRequestDTO request) {
        Product product = findProductById(id);
        if (!product.getSku().equals(request.getSku()) && productRepository.existsBySku(request.getSku())) {
            throw new BadRequestException("SKU already exists: " + request.getSku());
        }
        modelMapper.map(request, product);
        if (request.getSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(request.getSupplierId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier", request.getSupplierId()));
            product.setSupplier(supplier);
        }
        Product updated = productRepository.save(product);
        stockAlertService.checkAndCreateAlerts(updated);
        return toResponseDTO(updated);
    }

    @Override
    public ProductResponseDTO findById(Long id) {
        return toResponseDTO(findProductById(id));
    }

    @Override
    public ProductResponseDTO findBySku(String sku) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Product with SKU: " + sku));
        return toResponseDTO(product);
    }

    @Override
    public ProductResponseDTO findByBarcode(String barcode) {
        Product product = productRepository.findByBarcode(barcode)
                .orElseThrow(() -> new ResourceNotFoundException("Product with barcode: " + barcode));
        return toResponseDTO(product);
    }

    @Override
    public Page<ProductResponseDTO> findAll(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable).map(this::toResponseDTO);
    }

    @Override
    public Page<ProductResponseDTO> search(String query, Pageable pageable) {
        return productRepository.searchProducts(query, pageable).map(this::toResponseDTO);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Product product = findProductById(id);
        product.setActive(false);
        productRepository.save(product);
    }

    @Override
    @Transactional
    public void adjustStock(Long id, int quantity, String reason) {
        Product product = findProductById(id);
        int newQty = product.getQuantityInStock() + quantity;
        if (newQty < 0) {
            throw new BadRequestException("Insufficient stock. Current: " + product.getQuantityInStock());
        }
        product.setQuantityInStock(newQty);
        Product updated = productRepository.save(product);
        stockAlertService.checkAndCreateAlerts(updated);
    }

    @Override
    public List<ProductResponseDTO> getLowStockProducts() {
        return productRepository.findLowStockProducts().stream().map(this::toResponseDTO).toList();
    }

    @Override
    public List<ProductResponseDTO> getOutOfStockProducts() {
        return productRepository.findOutOfStockProducts().stream().map(this::toResponseDTO).toList();
    }

    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    private ProductResponseDTO toResponseDTO(Product product) {
        ProductResponseDTO dto = modelMapper.map(product, ProductResponseDTO.class);
        dto.setLowStock(product.getQuantityInStock() <= product.getMinimumStockLevel());
        if (product.getSupplier() != null) {
            dto.setSupplierId(product.getSupplier().getId());
            dto.setSupplierName(product.getSupplier().getName());
        }
        return dto;
    }
}
