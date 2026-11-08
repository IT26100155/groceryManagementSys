package com.grocerymanagementsys.staff.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** A picture above the size limit is rejected before the controller runs, so it is handled here. */
@ControllerAdvice
public class UploadExceptionAdvice {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String tooLarge(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", "The picture is too large (5 MB max).");
        return "redirect:/staff/inventory/items";
    }
}
