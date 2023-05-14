package kr.co.gkn.education.board.posts.repository;

import kr.co.gkn.education.board.posts.entity.Posts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostsRepository extends JpaRepository<Posts, Long> {

    List<Posts> findAllByOrderByIdDesc();
    List<Posts> findAllByContentContains(String word);
}
