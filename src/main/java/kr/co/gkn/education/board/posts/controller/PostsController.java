package kr.co.gkn.education.board.posts.controller;

import kr.co.gkn.education.board.posts.dto.PostsCreateRequestDto;
import kr.co.gkn.education.board.posts.dto.PostsListResponseDto;
import kr.co.gkn.education.board.posts.dto.PostsResponseDto;
import kr.co.gkn.education.board.posts.dto.PostsUpdateRequestDto;
import kr.co.gkn.education.board.posts.service.PostsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class PostsController {

    private final PostsService postsService;

    @PostMapping("/posts")
    public Long save(@RequestBody PostsCreateRequestDto requestDto) {
        return postsService.save(requestDto);
    }

    @PutMapping("/posts/{id}")
    public Long update(@PathVariable Long id, @RequestBody PostsUpdateRequestDto requestDto) {
        return postsService.update(id, requestDto);
    }

    @GetMapping("/posts")
    public List<PostsListResponseDto> findAllDesc() {
        return postsService.findAllDesc();
    }

    @GetMapping("/posts/{id}")
    public PostsResponseDto findById(@PathVariable Long id) {
        return postsService.findById(id);
    }

    @GetMapping("/posts/{id}/related")
    public List<PostsListResponseDto> findRelatedContent(@PathVariable Long id) throws IOException {
        return postsService.findRelatedContent(id);
    }
}
