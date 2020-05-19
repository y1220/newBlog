package it.course.myblog.service;

import it.course.myblog.entity.DBFile;
import it.course.myblog.exception.FileNotFoundException;
import it.course.myblog.exception.FileStorageException;
import it.course.myblog.exception.ResourceNotFoundException;
import it.course.myblog.repository.DBFileRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
public class DBFileStorageService {

    @Autowired
    private DBFileRepository dbFileRepository;

    public DBFile storeFile(MultipartFile file) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            DBFile dbFile = new DBFile(fileName, file.getContentType(), file.getBytes());

            return dbFileRepository.save(dbFile);
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public DBFile getFile(Long id) {
        return dbFileRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException("File not found with id " + id));
    }
}