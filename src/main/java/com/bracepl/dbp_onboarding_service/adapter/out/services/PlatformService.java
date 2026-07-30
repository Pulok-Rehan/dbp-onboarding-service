package com.bracepl.dbp_onboarding_service.adapter.out.services;

import com.bracepl.dbp_onboarding_service.adapter.out.entities.PlatformProfile;
import com.bracepl.dbp_onboarding_service.adapter.out.interfaces.PlatformProfileRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Service
@AllArgsConstructor
public class PlatformService {

    private final PlatformProfileRepository repository;

    public PlatformProfile create(PlatformProfile profile, MultipartFile imageFile) throws IOException {

        if (repository.existsByMobileNumber(profile.getMobileNumber())) {
            throw new RuntimeException("Platform profile already exists");
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            profile.setImage(Base64.getEncoder().encodeToString(imageFile.getBytes()));
        }

        return repository.save(profile);
    }

    public List<PlatformProfile> getAll() {
        return repository.findAll();
    }

//    public PlatformProfile getById(String id) {
//        return repository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Platform profile not found"));
//    }

    public PlatformProfile getByMobile(String mobileNumber) {
        return repository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new RuntimeException("Platform profile not found"));
    }

    public PlatformProfile update(String mobileNumber,
                                  PlatformProfile request,
                                  MultipartFile imageFile) throws IOException {

        PlatformProfile profile = repository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new RuntimeException("Platform profile not found"));

        if (request.getEmail() != null) {
            profile.setEmail(request.getEmail());
        }

        profile.setInvestor(request.isInvestor());

        if (imageFile != null && !imageFile.isEmpty()) {
            // Store as Base64
            profile.setImage(Base64.getEncoder().encodeToString(imageFile.getBytes()));

            // OR upload to S3/Cloudinary and save URL instead
            // profile.setImage(uploadService.upload(imageFile));
        }

        return repository.save(profile);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }

}
