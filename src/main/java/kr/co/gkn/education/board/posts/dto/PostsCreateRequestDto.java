package kr.co.gkn.education.board.posts.dto;

import kr.co.gkn.education.board.posts.entity.Posts;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class PostsCreateRequestDto {

    private String title;
    private String content;
    private LocalDateTime createAt;

    @Builder
    public PostsCreateRequestDto(String title, String content, LocalDateTime createAt) {
        this.title = title;
        this.content = content;
        this.createAt = createAt;
    }

    public Posts toEntity() {
        return Posts.builder()
                .title(title)
                .content(content)
                .createAt(createAt)
                .build();
    }
}
