// ValidFileCategory.java - 文件类别验证注解
package com.example.demo.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = com.example.demo.validation.FileValidator.FileCategoryValidator.class)
@Documented
public @interface ValidFileCategory {
    String message() default "无效的文件类别";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}