package com.streaming_app.entities;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "yt_video")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String videoId;

    private String title;

    private String videoDescription;

    private String contentType;

    private String filePath;


}
