package com.firefly.domain.configuration.web.controller.reference.transaction;

import com.firefly.domain.configuration.core.config.reference.transaction.TransactionCategoryConfigService;
import com.firefly.domain.configuration.interfaces.rest.reference.TransactionCategoryDto;
import com.firefly.domain.configuration.web.controller.reference.base.AbstractReferenceDataAdminController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/configuration/admin/transaction-categories")
@Tag(name = "Transaction Category Admin", description = "CRUD over transaction categories")
public class TransactionCategoryAdminController extends AbstractReferenceDataAdminController<TransactionCategoryDto, UUID> {
    public TransactionCategoryAdminController(TransactionCategoryConfigService service) {
        super(service);
    }
}
