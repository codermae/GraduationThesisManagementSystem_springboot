package com.example.demo.dto;

import lombok.Data;
import javax.validation.constraints.*;

@Data
public class ChangePasswordRequest {
    @NotBlank(message = "原密码不能为空")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "新密码长度必须在6-20个字符之间")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{6,20}$",
            message = "新密码必须包含至少一个大写字母、一个小写字母和一个数字")
    private String newPassword;
}