package com.streaming_app.controllers;

import com.streaming_app.entities.Video;
import com.streaming_app.exception.VideoNotFoundException;
import com.streaming_app.payloads.CustomMessage;
import com.streaming_app.services.VideoService;
import jakarta.annotation.Resource;
import org.apache.tomcat.util.file.ConfigurationSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/videos")
public class VideoController {

    private VideoService videoService;

    public  VideoController (VideoService videoService){
        this.videoService=videoService;
    }

    private static final Logger logger = LoggerFactory.getLogger(VideoController.class);

    @PostMapping("/create")
    public ResponseEntity<?>createVideo(
            @RequestParam("file")MultipartFile file,
            @RequestParam("title")String title,
            @RequestParam("description")String description
            ){
        Video video=new Video();
        video.setTitle(title);
        video.setVideoDescription(description);
        video.setVideoId(UUID.randomUUID().toString());

        Video savedVideo=videoService.save(video,file);

        if(savedVideo!=null){
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(video);
        }else {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomMessage.builder()
                            .message("Video not uploaded")
                            .success(false)
                            .build());
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<Video>getVideoById(@PathVariable("id") String id){
        try {
            Video video=videoService.get(id);
            return new ResponseEntity<>(video,HttpStatus.OK);
        }catch (VideoNotFoundException e){
            logger.warn(e.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @GetMapping("/title/{title}")
    public ResponseEntity<Video>getVideoByTitle(@PathVariable("title") String title){
        try {
            Video video=videoService.getByTitle(title);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (VideoNotFoundException e){
            logger.warn(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/all")
    public ResponseEntity<List<Video>> getAllVideos() {
        List<Video> videos = videoService.getAll();
        return new ResponseEntity<>(videos, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public  ResponseEntity<Video> updateVideo(@PathVariable String id,@RequestBody Video updatedVideoDetails){
        try{
            Video updatedVideo=videoService.updateById(id,updatedVideoDetails);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (VideoNotFoundException e){
            logger.warn(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/stream/{id}")
    public ResponseEntity<Resource>stream(@PathVariable String id){
        Video video=videoService.get(id);

        String contentType=video.getContentType();
        String filePath=video.getFilePath();

        Resource resource= (Resource) new FileSystemResource(filePath);

        if (contentType.isEmpty()){
            contentType="application/octet-stream";
        }
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    //stream video in chunks
    @GetMapping("/stream/range/{id}")
    public ResponseEntity<Resource>streamVideoRange(@PathVariable String id,@RequestHeader(value="Range", required = false)String range){
        //1.Get video and its path
        Video video=videoService.get(id);
        Path path= Paths.get(video.getFilePath());

        //Create a resource
        Resource resource= (Resource) new FileSystemResource(path);

        String contentType= video.getContentType();

        if(contentType==null){
            contentType="application/octet-stream";
        }

        //file length
        long fileLength=path.toFile().length();

        //check if range's value is null
        if(range==null){
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        }
        long rangeStart;

        long rangeEnd;

        String[]ranges=range.replace("bytes","").split("-");
        rangeStart=Long.parseLong(ranges[0]);

        if(ranges.length>1){
            rangeEnd=Long.parseLong(ranges[1]);
        }else{
            rangeEnd=fileLength-1;
        }
        if(rangeEnd>fileLength-1){
            rangeEnd=fileLength-1;
        }
        InputStream inputStream;

        try {
            inputStream= Files.newInputStream(path);
            inputStream.skip(rangeStart);
        }catch (IOException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        long contentLength=rangeEnd-rangeStart+1;
        HttpHeaders headers=new HttpHeaders();
        headers.add("Content-Range","bytes="+rangeStart+"-"+rangeEnd+"/"+fileLength);
        headers.add("Accept-Ranges", "bytes");

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .headers(headers)
                .contentType(MediaType.parseMediaType(contentType))
                .body((Resource) new InputStreamResource(inputStream));
    }
}


