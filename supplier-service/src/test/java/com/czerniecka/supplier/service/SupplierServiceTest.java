package com.czerniecka.supplier.service;

import com.czerniecka.supplier.dto.SupplierDTO;
import com.czerniecka.supplier.dto.SupplierMapper;
import com.czerniecka.supplier.entity.Supplier;
import com.czerniecka.supplier.repository.SupplierRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public  class SupplierServiceTest {

    @Mock
    SupplierRepository supplierRepository;
    @Spy
    SupplierMapper supplierMapper = Mappers.getMapper(SupplierMapper.class);

    SupplierService supplierService;

    @Before
    public void init(){
        supplierService = new SupplierService(supplierRepository, supplierMapper);
    }

    @Test
    public void shouldReturnSupplier(){

        UUID supplierId = UUID.randomUUID();
        Supplier supplier = new Supplier(supplierId, "Supplier", "supplier@gmail.com");

        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(supplier));
        Optional<SupplierDTO> supplierById =
                supplierService.findSupplierById(supplierId);

        assertEquals(Optional.of(supplierMapper.toSupplierDTO(supplier)), supplierById);

    }

    @Test
    public void shouldReturnSavedSupplier(){

        Supplier supplier = new Supplier(UUID.randomUUID(), "Supplier", "supplier@gmail.com");
        SupplierDTO supplierDTO = new SupplierDTO();

        when(supplierRepository.save(supplierMapper.toSupplier(supplierDTO))).thenReturn(supplier);
        SupplierDTO saved = supplierService.save(supplierDTO);

        assertThat(saved.getEmail()).isEqualTo("supplier@gmail.com");
        assertThat(saved.getName()).isEqualTo("Supplier");
    }


}