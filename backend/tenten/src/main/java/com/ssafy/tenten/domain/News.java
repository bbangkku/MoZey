package com.ssafy.tenten.domain;

import com.ssafy.tenten.dto.NewsDto;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@DynamicInsert
@Table(name = "NEWS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long newsId;

    @Column
    private String title;

    @Column
    private String content;

    @Column
    private String company;

    @Column
    private LocalDate date;

    @Column
    private String imageUrl;


    @Builder
    News(NewsDto newsDto){
        this.newsId = newsDto.getNewsId();
        this.title = newsDto.getTitle();
        this.content = newsDto.getContent();
        this.company = newsDto.getCompany();
        this.date = newsDto.getDate();
        this.imageUrl = newsDto.getImageUrl();

    }
}
