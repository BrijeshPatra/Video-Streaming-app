package com.streaming_app.services;

import com.streaming_app.entities.Video;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VideoService {

    //save video
    Video save(Video video, MultipartFile file);

    //get video by id
    Video get(String id);

    //get video by title
    Video getByTitle(String title);

    Video updateById(String id,Video updatedDetails);

    List<Video> getAll();
}
