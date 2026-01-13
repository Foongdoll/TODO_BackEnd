package com.foongdoll.portfolio.todoongs.chat.service;

import com.foongdoll.portfolio.todoongs.chat.entity.ChatAttachment;
import com.foongdoll.portfolio.todoongs.chat.model.ChatAttachmentType;
import com.foongdoll.portfolio.todoongs.chat.repository.ChatAttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final ChatAttachmentRepository chatAttachmentRepository;

    @Value("${chat.upload-dir:./uploads/chat}")
    private String uploadDir;

    public ChatAttachment store(MultipartFile file) throws IOException {
        String originalName = StringUtils.hasText(file.getOriginalFilename())
                ? file.getOriginalFilename()
                : "file";
        String extension = "";
        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex > -1) {
            extension = originalName.substring(dotIndex);
        }

        String storedName = UUID.randomUUID().toString() + extension;
        Path basePath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(basePath);

        Path target = basePath.resolve(storedName);
        file.transferTo(target.toFile());

        String contentType = file.getContentType();
        ChatAttachmentType type = resolveType(contentType);
        String url = "/uploads/chat/" + storedName;

        ChatAttachment attachment = new ChatAttachment();
        attachment.setType(type);
        attachment.setOriginalName(originalName);
        attachment.setStoredName(storedName);
        attachment.setUrl(url);
        attachment.setMime(contentType);
        attachment.setSize(file.getSize());

        return chatAttachmentRepository.save(attachment);
    }

    private ChatAttachmentType resolveType(String contentType) {
        if (contentType == null) {
            return ChatAttachmentType.FILE;
        }
        if (contentType.startsWith("image/")) {
            return ChatAttachmentType.IMAGE;
        }
        if (contentType.startsWith("video/")) {
            return ChatAttachmentType.VIDEO;
        }
        if (contentType.startsWith("audio/")) {
            return ChatAttachmentType.AUDIO;
        }
        return ChatAttachmentType.FILE;
    }
}
