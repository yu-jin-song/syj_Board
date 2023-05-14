package kr.co.gkn.education.board.posts.dto;

import kr.co.gkn.education.board.posts.entity.Posts;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostsListResponseDto {

    private Long id;
    private String title;
    private LocalDateTime createAt;

    public PostsListResponseDto(Posts entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.createAt = entity.getCreateAt();
    }
}
