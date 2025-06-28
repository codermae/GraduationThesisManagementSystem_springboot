// FileValidator.java - 文件验证器
package com.example.demo.validation;

import com.example.demo.constant.FileConstants;
import com.example.demo.validation.ValidFileCategory;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class FileValidator {

    /**
     * 验证文件类别
     */
    public static class FileCategoryValidator implements ConstraintValidator<ValidFileCategory, String> {
        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (value == null) {
                return false;
            }
            return FileConstants.CATEGORY_PROPOSAL.equals(value) ||
                    FileConstants.CATEGORY_REPORT.equals(value) ||
                    FileConstants.CATEGORY_THESIS.equals(value) ||
                    FileConstants.CATEGORY_OTHER.equals(value);
        }
    }
}