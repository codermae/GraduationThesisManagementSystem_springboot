// GradeBatchRequest.java - 批量评分请求
package com.example.demo.dto;

import lombok.Data;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class GradeBatchRequest {
    @NotEmpty(message = "批量评分列表不能为空")
    @Valid
    private List<ThesisScoreRequest> thesisScores;

    @Valid
    private List<DefenseScoreRequest> defenseScores;
}