package com.grocerymanagementsys.order.dtos;

import java.math.BigDecimal;

public record OrderUnitDto(
<<<<<<< Updated upstream
        Integer itemId,
=======
        Long itemId,
>>>>>>> Stashed changes
        Integer quantity,
        BigDecimal unitPrice
) { }
