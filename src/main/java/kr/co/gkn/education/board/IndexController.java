package kr.co.gkn.education.board;

import kr.co.gkn.education.board.posts.dto.PostsListResponseDto;
import kr.co.gkn.education.board.posts.dto.PostsResponseDto;
import kr.co.gkn.education.board.posts.service.PostsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class IndexController {

    private final PostsService postsService;

    @GetMapping("/")
    public String index(Model model) {
        List<PostsListResponseDto> dtoList = postsService.findAllDesc();
        model.addAttribute("posts", dtoList);
        return "index";
    }

    @GetMapping("/posts/save")
    public String postsSave() {
        return "posts-save";
    }

    @GetMapping("/posts/update/{id}")
    public String postsUpdate(@PathVariable Long id, Model model) {
        PostsResponseDto dto = postsService.findById(id);
        model.addAttribute("post", dto);

        return "posts-update";
    }

    @GetMapping("/posts/read/{id}")
    public String postsReadDetail(@PathVariable Long id, Model model) throws IOException {
        PostsResponseDto dto = postsService.findById(id);
        List<PostsListResponseDto> dtoList = postsService.findRelatedContent(id);
        model.addAttribute("post", dto);
        model.addAttribute("relatedPosts", dtoList);

        return "posts-detail";
    }
}