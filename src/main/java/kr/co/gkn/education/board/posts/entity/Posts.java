package kr.co.gkn.education.board.posts.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "posts")
public class Posts {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue
    private Long id;

    @Column(length = 500, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "create_at")
    private LocalDateTime createAt;


    @Builder
    public Posts(String title, String content, LocalDateTime createAt) {
        this.title = title;
        this.content = content;
        this.createAt = createAt;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
