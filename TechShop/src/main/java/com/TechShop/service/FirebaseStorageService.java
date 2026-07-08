package com.TechShop.service;

import com.google.cloud.storage.Blob;
import com.google.firebase.cloud.StorageClient;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FirebaseStorageService {

    @Value("${firebase.storage.path}")
    private String storagePath;

    public String uploadImage(MultipartFile localFile, String folder, Integer id) throws IOException {
        String originalName = localFile.getOriginalFilename();
        String extension = "";

        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }

        String fileName = "img" + getFormattedNumber(id) + extension;
        String rutaArchivo = storagePath + "/" + folder + "/" + fileName;

        Blob blob = StorageClient.getInstance()
                .bucket()
                .create(rutaArchivo, localFile.getBytes(), localFile.getContentType());

        return blob.signUrl(1825, TimeUnit.DAYS).toString();
    }

    private String getFormattedNumber(long id) {
        return String.format("%014d", id);
    }
}