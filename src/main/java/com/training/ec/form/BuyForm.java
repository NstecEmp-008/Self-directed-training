package com.training.ec.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BuyForm {
    
    @NotNull(message = "株IDは必須です")
    private Long stockId;

    @NotNull(message = "購入数は必須です")
    @Min(value = 1, message = "購入数は1株以上である必要があります")
    private Integer quantity;
}