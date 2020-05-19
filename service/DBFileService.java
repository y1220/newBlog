package it.course.myblog.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import it.course.myblog.entity.DBFile;
import it.course.myblog.repository.DBFileRepository;

@Service
public class DBFileService {

    @Autowired
    DBFileRepository dbFileRepository;

    public DBFile fromMultiToDBFile(MultipartFile file) {

    	String fileName = StringUtils.cleanPath(file.getOriginalFilename());

    	DBFile dbFile = null;
		try {
			dbFile = new DBFile(fileName, file.getContentType(), file.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}

    	return dbFile;
    }
    
}