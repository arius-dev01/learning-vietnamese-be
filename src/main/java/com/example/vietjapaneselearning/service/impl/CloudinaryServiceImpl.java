package com.example.vietjapaneselearning.service.impl;

import com.cloudinary.utils.ObjectUtils;
import com.example.vietjapaneselearning.config.CloudinaryConfig;
import com.example.vietjapaneselearning.model.User;
import com.example.vietjapaneselearning.repository.UserRepository;
import com.example.vietjapaneselearning.service.ICloudinaryService;
import jakarta.persistence.EntityNotFoundException;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryServiceImpl implements ICloudinaryService {
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    @Autowired
    private CloudinaryConfig cloudinaryConfig;
    public CloudinaryServiceImpl(UserRepository userRepository, CurrentUserService currentUserService) {
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
    }

    @Override
    public String upload(MultipartFile file) throws IOException {
        User user = userRepository.findById(currentUserService.getUserCurrent().getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Thumbnails.of(bufferedImage)
                .size(1024, 1024)
                .outputFormat("jpg")
                .outputQuality(0.8)
                .toOutputStream(byteArrayOutputStream);
        byte[] resizedByteArray = byteArrayOutputStream.toByteArray();
        Map<?, ?> uploadResult = cloudinaryConfig.cloudinary().uploader().upload(resizedByteArray,
                ObjectUtils.asMap(
                        "resource_type", "image",
                        "format", "jpg",
                        "use_filename", true
                ));

        user.setAvatar(uploadResult.get("secure_url").toString());
        userRepository.save(user);
        return uploadResult.get("secure_url").toString();
    }
}
