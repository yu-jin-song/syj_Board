package kr.co.gkn.education.board.posts.dto;

import kr.co.gkn.education.board.posts.entity.Posts;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostsResponseDto {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime createAt;

    public PostsResponseDto(Posts entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.content = entity.getContent();
        this.createAt = entity.getCreateAt();
    }
}
