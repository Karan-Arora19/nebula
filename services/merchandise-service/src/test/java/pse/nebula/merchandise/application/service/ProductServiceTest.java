package pse.nebula.merchandise.application.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import pse.nebula.merchandise.application.dto.ProductRequest;
import pse.nebula.merchandise.application.dto.ProductResponse;
import pse.nebula.merchandise.domain.model.Product;
import pse.nebula.merchandise.domain.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void findAll_mapsEntitiesToResponses() {
        Product product = Product.builder()
                .id(1L)
                .name("Hat")
                .description("Blue hat")
                .price(BigDecimal.TEN)
                .stock(5)
                .imageUrl("img")
                .build();
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<ProductResponse> responses = productService.findAll();

        assertEquals(1, responses.size());
        assertEquals("Hat", responses.get(0).getName());
        assertEquals(BigDecimal.TEN, responses.get(0).getPrice());
    }

    @Test
    void findById_notFound_throwsNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> productService.findById(1L));
    }

    @Test
    void create_persistsProductAndReturnsResponse() {
        ProductRequest request = new ProductRequest("Hat", "Blue hat", BigDecimal.valueOf(12.50), 3, "img");
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product toSave = invocation.getArgument(0);
            toSave.setId(99L);
            return toSave;
        });

        ProductResponse response = productService.create(request);

        verify(productRepository).save(argThat(p -> p.getName().equals("Hat") && p.getStock() == 3));
        assertEquals(99L, response.getId());
        assertEquals("Hat", response.getName());
        assertEquals(BigDecimal.valueOf(12.50), response.getPrice());
    }
}
