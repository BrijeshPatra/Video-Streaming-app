package com.streaming_app.services.impl;

import com.streaming_app.entities.Video;
import com.streaming_app.exception.VideoNotFoundException;
import com.streaming_app.repositories.VideoRepository;
import com.streaming_app.services.VideoService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Service
public class VideoServiceImpl implements VideoService {

    private static final Logger logger = LoggerFactory.getLogger(VideoServiceImpl.class);
    private VideoRepository videoRepository;

    public VideoServiceImpl(VideoRepository videoRepository){
        this.videoRepository=videoRepository;
    }

    @Value("${files.video}")
    String DIR;

    @PostConstruct
    public void init(){
        File file=new File(DIR);

        if(!file.exists()){
            file.mkdir();
            System.out.println("Folder created");
        }else {
            System.out.println("Folder exists");
        }
    }
    @Override
    public Video save(Video video, MultipartFile file) {

        //Return original filename
        try {
            String filename = file.getOriginalFilename();
            String contentType = file.getContentType();
            InputStream inputStream = file.getInputStream();

            //create path for folder

            //file path
            String clean_filename=StringUtils.cleanPath(filename);

            //folder path
            String clean_folder=StringUtils.cleanPath(DIR);

            //folder path with filename
            Path path= Paths.get(clean_filename,clean_folder);


            //copy file to the folder
            Files.copy(inputStream,path, StandardCopyOption.REPLACE_EXISTING);

            //video metadata
            video.setContentType(contentType);
            video.setFilePath(path.toString());

            //metadata save
            videoRepository.save(video);

        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
        return video;
    }

    @Override
    public Video get(String id) {
       return videoRepository.findById(id)
               .orElseThrow(()->{
                   logger.warn("Video not found with ID: {}",id);
                   return new VideoNotFoundException("Video not found with ID");
               });
    }

    @Override
    public Video getByTitle(String title) {
        videoRepository.findByTitle(title)
                .orElseThrow(()->{
                    logger.warn("Video title not found " + title);
                    return new VideoNotFoundException("Video title not found ");
                });

        return null;
    }

    @Override
    public Video updateById(String id,Video updatedDetails) {
        Optional<Video>existingVideoOptional=videoRepository.findById(id);
        if(existingVideoOptional.isPresent()){
            Video existingVideo=existingVideoOptional.get();

            existingVideo.setTitle(updatedDetails.getTitle());
            existingVideo.setVideoDescription(updatedDetails.getVideoDescription());
            existingVideo.setContentType(updatedDetails.getContentType());
            existingVideo.setFilePath(updatedDetails.getFilePath());

            videoRepository.save(existingVideo);
            return existingVideo;
        }else {
            throw new VideoNotFoundException("Video not updated");
        }
    }

    @Override
    public List<Video> getAll() {
        return videoRepository.findAll();
    }
}
