package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.dto.MessageDTO;
import com.example.socialmeet.entity.Message;
import com.example.socialmeet.repository.MessageRepository;
import com.example.socialmeet.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class MessageController {
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<MessageDTO>>> getMessages(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            List<Message> messages = messageRepository.findByReceiverIdOrderByCreatedAtDesc(userId);
            
            List<MessageDTO> messageDTOs = messages.stream()
                    .map(MessageDTO::new)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success(messageDTOs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取消息列表失败: " + e.getMessage()));
        }
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<MessageDTO>> sendMessage(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody MessageDTO messageDTO) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            Long senderId = jwtUtil.getUserIdFromToken(jwt);
            
            Message message = new Message();
            message.setSenderId(senderId);
            message.setReceiverId(messageDTO.getReceiverId());
            message.setContent(messageDTO.getContent());
            message.setMessageType(messageDTO.getMessageType());
            message.setIsRead(false);
            
            message = messageRepository.save(message);
            
            return ResponseEntity.ok(ApiResponse.success(new MessageDTO(message)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("发送消息失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/conversations")
    public ResponseEntity<ApiResponse<List<MessageDTO>>> getConversations(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            // TODO: 实现获取会话列表功能
            return ResponseEntity.ok(ApiResponse.success(List.of()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取会话列表失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<MessageDTO>>> getMessagesWithUser(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long userId) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            Long currentUserId = jwtUtil.getUserIdFromToken(jwt);
            
            List<Message> messages = messageRepository.findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByCreatedAtAsc(
                    currentUserId, userId, currentUserId, userId);
            
            List<MessageDTO> messageDTOs = messages.stream()
                    .map(MessageDTO::new)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success(messageDTOs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取消息失败: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<String>> markAsRead(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            Message message = messageRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("消息不存在"));
            
            if (!message.getReceiverId().equals(userId)) {
                return ResponseEntity.badRequest().body(ApiResponse.error("无权限标记此消息"));
            }
            
            message.setIsRead(true);
            messageRepository.save(message);
            
            return ResponseEntity.ok(ApiResponse.success("标记已读成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("标记已读失败: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteMessage(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            Message message = messageRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("消息不存在"));
            
            if (!message.getSenderId().equals(userId) && !message.getReceiverId().equals(userId)) {
                return ResponseEntity.badRequest().body(ApiResponse.error("无权限删除此消息"));
            }
            
            messageRepository.delete(message);
            
            return ResponseEntity.ok(ApiResponse.success("删除消息成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("删除消息失败: " + e.getMessage()));
        }
    }
}
