package com.foongdoll.portfolio.todoongs.chat.controller;

import com.foongdoll.portfolio.todoongs.chat.dto.ChatAttachmentResponse;
import com.foongdoll.portfolio.todoongs.chat.entity.ChatAttachment;
import com.foongdoll.portfolio.todoongs.chat.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/chat/uploads")
@RequiredArgsConstructor
public class ChatUploadController {

    private final FileStorageService fileStorageService;

    @PostMapping
    public ResponseEntity<ChatAttachmentResponse> upload(@RequestParam("file") MultipartFile file) throws IOException {
        ChatAttachment attachment = fileStorageService.store(file);
        ChatAttachmentResponse response = ChatAttachmentResponse.builder()
                .attachmentId(attachment.getPk())
                .type(attachment.getType())
                .name(attachment.getOriginalName())
                .url(attachment.getUrl())
                .mime(attachment.getMime())
                .size(attachment.getSize())
                .build();
        return ResponseEntity.ok(response);
    }
}
