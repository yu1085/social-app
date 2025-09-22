package com.example.socialmeet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

/**
 * 发布动态请求DTO
 */
@Data
public class PublishDynamicRequest {
    
    @NotBlank(message = "动态内容不能为空")
    @Size(max = 140, message = "动态内容不能超过140个字符")
    private String content;
    
    private List<String> images;
    
    @Size(max = 100, message = "位置信息不能超过100个字符")
    private String location;
}
