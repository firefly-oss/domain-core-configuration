package com.firefly.domain.configuration.web.controller.reference.base;

import com.firefly.domain.configuration.core.config.reference.ReferenceDataService;
import com.firefly.domain.configuration.interfaces.rest.reference.ReferenceDataFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Generic REST contract for every reference-data admin controller. Concrete
 * subclasses supply only the {@code @RestController}, {@code @RequestMapping}
 * and {@code @Tag} annotations plus a constructor that passes the typed
 * {@link ReferenceDataService}. The CRUD endpoints themselves are inherited
 * unchanged.
 *
 * @param <D>  public domain DTO type
 * @param <I>  identifier type (typically {@link java.util.UUID})
 */
public abstract class AbstractReferenceDataAdminController<D, I> {

    protected final ReferenceDataService<D, I> service;

    protected AbstractReferenceDataAdminController(ReferenceDataService<D, I> service) {
        this.service = service;
    }

    @Operation(summary = "Create reference-data entity",
            description = "Persists a new master-data entry for the bound entity type.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "409", description = "Duplicate or conflicting entity"),
            @ApiResponse(responseCode = "500", description = "Unexpected error")
    })
    @PostMapping
    public Mono<ResponseEntity<I>> create(@Valid @RequestBody D dto) {
        return service.create(dto)
                .map(id -> ResponseEntity.status(201).body(id));
    }

    @Operation(summary = "Update reference-data entity",
            description = "Applies the incoming DTO over the existing entity.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Entity not found"),
            @ApiResponse(responseCode = "409", description = "Conflicting state"),
            @ApiResponse(responseCode = "500", description = "Unexpected error")
    })
    @PutMapping("/{id}")
    public Mono<ResponseEntity<D>> update(@PathVariable("id") I id, @Valid @RequestBody D dto) {
        return service.update(id, dto).map(ResponseEntity::ok);
    }

    @Operation(summary = "Remove reference-data entity",
            description = "Deletes the referenced master-data record.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Removed"),
            @ApiResponse(responseCode = "404", description = "Entity not found"),
            @ApiResponse(responseCode = "409", description = "Entity is still referenced"),
            @ApiResponse(responseCode = "500", description = "Unexpected error")
    })
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> remove(@PathVariable("id") I id) {
        return service.remove(id).thenReturn(ResponseEntity.noContent().build());
    }

    @Operation(summary = "Get reference-data entity by identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Found"),
            @ApiResponse(responseCode = "404", description = "Entity not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected error")
    })
    @GetMapping("/{id}")
    public Mono<ResponseEntity<D>> getById(@PathVariable("id") I id) {
        return service.getById(id).map(ResponseEntity::ok);
    }

    @Operation(summary = "List reference-data entities",
            description = "Returns a filtered, paged collection of master-data entries.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Collection returned"),
            @ApiResponse(responseCode = "400", description = "Invalid filter arguments"),
            @ApiResponse(responseCode = "500", description = "Unexpected error")
    })
    @GetMapping
    public Flux<D> list(@ModelAttribute ReferenceDataFilter filter) {
        return service.list(filter);
    }
}
