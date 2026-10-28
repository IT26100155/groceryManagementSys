<<<<<<< Updated upstream
package com.grocerymanagementsys.order.dtos;

import jakarta.validation.constraints.NotNull;

public record PlaceOrderDto(
        @NotNull Integer customerId,
        @NotNull Integer addressId
) { }
=======
package com.grocerymanagementsys.order.dtos;

import jakarta.validation.constraints.NotNull;

public record PlaceOrderDto(
        @NotNull Integer customerId,
        @NotNull Integer addressId
) { }
>>>>>>> Stashed changes
