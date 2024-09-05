package com.streaming_app.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "yt_courses")
@Data
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String courseId;

    private String courseTitle;

//    @OneToMany(mappedBy = "course")
//    private List<Video> list=new ArrayList<>();
}
