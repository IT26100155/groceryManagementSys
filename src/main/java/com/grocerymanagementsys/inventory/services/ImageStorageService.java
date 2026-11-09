package com.grocerymanagementsys.inventory.services;

import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

@Service
public class ImageStorageService {
    public static final String WEB_PREFIX = "/uploads/items/";
    private static final Map<String, String> EXTENSIONS = Map.of(
            "image/jpeg", ".jpg", "image/png", ".png", "image/gif", ".gif", "image/webp", ".webp");

    private final Path itemsDir;

    public ImageStorageService(@Value("${app.upload-dir:uploads}") String uploadDir) throws IOException {
        this.itemsDir = Path.of(uploadDir).toAbsolutePath().normalize().resolve("items");
        Files.createDirectories(itemsDir);
    }

    public Path getUploadRoot() {
        return itemsDir.getParent();
    }

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String extension = EXTENSIONS.get(file.getContentType());
        if (extension == null) {
            throw new ValidationException("The picture must be a JPG, PNG, GIF or WebP image.");
        }
        try {
            byte[] head = new byte[12];
            int read;
            try (InputStream in = file.getInputStream()) {
                read = in.readNBytes(head, 0, head.length);
            }

            if (!looksLike(extension, head, read)) {
                throw new ValidationException("That file is not a valid " + extension.substring(1).toUpperCase() + " image.");
            }

            String name = UUID.randomUUID() + extension;
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, itemsDir.resolve(name));
            }
            return WEB_PREFIX + name;
        } catch (IOException e) {
            throw new IllegalStateException("Could not save the picture.", e);
        }
    }

    public void delete(String webPath) {
        if (webPath == null || !webPath.startsWith(WEB_PREFIX)) {
            return;
        }
        try {
            Path target = itemsDir.resolve(Path.of(webPath).getFileName().toString()).normalize();
            if (target.startsWith(itemsDir)) {
                Files.deleteIfExists(target);
            }
        } catch (IOException | RuntimeException ignored) {
        }
    }

    private static boolean looksLike(String extension, byte[] b, int n) {
        if (n < 4) return false;
        return switch (extension) {
            case ".jpg" -> (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF;
            case ".png" -> (b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G';
            case ".gif" -> b[0] == 'G' && b[1] == 'I' && b[2] == 'F' && b[3] == '8';
            case ".webp" -> n >= 12 && b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F'
                    && b[8] == 'W' && b[9] == 'E' && b[10] == 'B' && b[11] == 'P';
            default -> false;
        };
    }
}
