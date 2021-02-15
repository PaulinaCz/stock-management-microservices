package com.czerniecka.inventory.controller;

import com.czerniecka.inventory.dto.InventoryDTO;
import com.czerniecka.inventory.service.InventoryService;
import com.czerniecka.inventory.vo.InventoryProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/inventories")
public class InventoryController {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping(value = "", produces = "application/json")
    public Flux<InventoryDTO> getAllInventory(){
        return inventoryService.findAll();
    }

    @GetMapping("/products")
    public Flux<List<InventoryProductResponse>> getAllWithProducts(){
        return inventoryService.findAllWithProducts();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public Mono<InventoryProductResponse> getInventoryById(@PathVariable("id") String inventoryId){

        return inventoryService.findInventoryById(inventoryId)
                .switchIfEmpty(Mono.error(new Exception(inventoryId)));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/product/{id}")
    public Mono<InventoryDTO> getInventoryByProductId(@PathVariable("id") String productId){
        return inventoryService.findInventoryByProductId(productId)
                .switchIfEmpty(Mono.error(new Exception("for product " + productId)));

    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    public Mono<InventoryDTO> addInventory(@RequestBody @Valid InventoryDTO inventoryDTO){
        return inventoryService.save(inventoryDTO)
                .switchIfEmpty(Mono.error(new Error(inventoryDTO.getId())));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PutMapping("/{id}")
    public Mono<InventoryDTO> updateInventory(@PathVariable("id") String inventoryId,
                                @RequestBody @Valid InventoryDTO inventoryDTO){

        return inventoryService.updateInventory(inventoryId, inventoryDTO)
                .switchIfEmpty(Mono.error(new Exception(inventoryId)));
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(Exception.class)
    public Map<String, Object> handleNotFound(Exception ex){

        Map<String, Object> errorBody = new HashMap<>();

        errorBody.put("timestamp", LocalDateTime.now());
        errorBody.put("error", "Inventory " + ex.getMessage() + " not found");

        return errorBody;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Error.class)
    public Map<String, Object> handleNotFoundForProduct(Exception ex){

        Map<String, Object> errorBody = new HashMap<>();

        errorBody.put("timestamp", LocalDateTime.now());
        errorBody.put("error", "Inventory " + ex.getMessage() + " not saved");

        return errorBody;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {

        Map<String, Object> errorBody = new HashMap<>();

        errorBody.put("timestamp", LocalDateTime.now());

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        errorBody.put("validationErrors", errors);
        return errorBody;
    }

}
