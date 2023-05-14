package kr.co.gkn.education.board.posts.service;

import kr.co.gkn.education.board.posts.dto.PostsCreateRequestDto;
import kr.co.gkn.education.board.posts.dto.PostsListResponseDto;
import kr.co.gkn.education.board.posts.dto.PostsResponseDto;
import kr.co.gkn.education.board.posts.dto.PostsUpdateRequestDto;
import kr.co.gkn.education.board.posts.entity.Posts;
import kr.co.gkn.education.board.posts.repository.PostsRepository;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PostsService {

    private final PostsRepository postsRepository;


    @Transactional
    public Long save(PostsCreateRequestDto requestDto) {
        requestDto.setCreateAt(LocalDateTime.now());
        return postsRepository.save(requestDto.toEntity()).getId();
    }

    @Transactional
    public Long update(Long id, PostsUpdateRequestDto requestDto) {
        Posts posts = postsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        posts.update(requestDto.getTitle(), requestDto.getContent());

        return id;
    }

    @Transactional(readOnly = true)
    public List<PostsListResponseDto> findAllDesc() {
        return postsRepository.findAllByOrderByIdDesc().stream()
                .map(PostsListResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PostsResponseDto findById(Long id) {
        Posts posts = postsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));

        return new PostsResponseDto(posts);
    }

    @Transactional(readOnly = true)
    public List<PostsListResponseDto> findRelatedContent(Long id) throws IOException {
        Posts selectedPosts = postsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));
        List<Posts> postsList = postsRepository.findAll();

        String selectedContent = selectedPosts.getContent();

        List<Object> morphAnalysisList = analyzeMorph(selectedContent);
        Map<String, Integer> wordMap = (Map<String, Integer>) morphAnalysisList.get(1);

        int boardAllCnt = postsList.size();
        List<String> removeWordList = new ArrayList<>();
        for(String word : wordMap.keySet()) {
            int boardCnt = postsRepository.findAllByContentContains(word).size();

            // 전체 게시글 중 60% 이상 발견 단어 목록
            if ((float)boardAllCnt / boardCnt >= 0.6f) {
                removeWordList.add(word);
                continue;
            }
        }

        // 60% 이상 발견 단어 배제
        for(String word : removeWordList) {
            wordMap.remove(word);
        }

        // 조회중인 게시물 제외
        postsList.remove(selectedPosts);

        // 연관게시글 판단(40%이하 빈도 단어 2개 이상 동시 존재)
        String content;
        int wordTotalCnt;
        int usedWordCnt;
        int relatedContentCnt = 0;
        int relatedWordCnt = 0;
        boolean isRelated = false;

        Map<Posts, List<String>> relatedBoardMap = new HashMap<>();
        Map<Posts, Map<Integer, Float>> relatedRateMap = new HashMap<>();

        for(Posts posts : postsList) {
            content = posts.getContent();
            wordTotalCnt = (int) analyzeMorph(content).get(0);  // 게시글의 총 단어 개수 구하기

            List<String> relatedWordList = new ArrayList<>();

            for(String word : wordMap.keySet()) {
                usedWordCnt = StringUtils.countOccurrencesOf(content, word);

                if((float) usedWordCnt / wordTotalCnt <= 0.4f) {
                    relatedContentCnt++;
                    relatedWordList.add(word);
                }
            }

            if(relatedContentCnt >= 2) {
                relatedBoardMap.put(posts, relatedWordList);
            }
        }

        Map<String, Integer> frequencyWordMap = new HashMap<>();
        Map<Posts, Integer> boardFrequencyMap = new HashMap<>();
        int onlyRelatedCnt = 0;
        float relatedRate = 0.0f;
        // 연관게시글 연관도 파악
        for(Posts posts : relatedBoardMap.keySet()) {
            content = posts.getContent();

            morphAnalysisList = analyzeMorph(content);
            wordTotalCnt = (int) morphAnalysisList.get(0);
            wordMap = (Map<String, Integer>) morphAnalysisList.get(1);

            //40% 이하 빈도 나타나는 모든 단어들 추출
            for(String word : wordMap.keySet()) {
                usedWordCnt = StringUtils.countOccurrencesOf(content, word);

                if((float) usedWordCnt / wordTotalCnt <= 0.4f) {
                    // 연관단어인지 판단
                    if(relatedBoardMap.values().contains(word)) {
                        onlyRelatedCnt += usedWordCnt;
                    }
                }
            }

            //연관도 구하기(연관게시글 보여지는 순서 결정)
            relatedRate = (float) onlyRelatedCnt / wordTotalCnt;
            Map<Integer, Float> rateCntMap = new HashMap<>();
            rateCntMap.put(onlyRelatedCnt, relatedRate);
            relatedRateMap.put(posts, rateCntMap);
        }

        // 빈도 내림차순 정렬
        List<Posts> resultList = new ArrayList<>(relatedRateMap.keySet());
        resultList.sort(new Comparator<Posts>() {
            @Override
            public int compare(Posts o1, Posts o2) {
                int result = 0;

                Map<Integer, Float> rateCntMap1 = relatedRateMap.get(o1);
                float rate1 = 0.0f;
                for (int key : rateCntMap1.keySet()) {
                    rate1 = rateCntMap1.get(key);
                }

                Map<Integer, Float> rateCntMap2 = relatedRateMap.get(o2);
                float rate2 = 0.0f;
                for (int key : rateCntMap2.keySet()) {
                    rate2 = rateCntMap2.get(key);
                }

                if(rate2 > rate1) {
                    result = 1;
                }

                if(rate1 < rate2) {
                    result = -1;
                }

                return result;
            }
        });

        return resultList.stream().limit(5)
                .map(PostsListResponseDto::new)
                .collect(Collectors.toList());
    }

    public List<Object> analyzeMorph(String content) {
        Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);
        KomoranResult analyzeResultList = komoran.analyze(content);
        List<Token> tokenList = analyzeResultList.getTokenList();

        Map<String, Integer> map = new HashMap<>();

        StringTokenizer st;
        String morph;

        int totalCnt = 0;
        int wordCnt = 0;

        // 단어별 갯수 세기
        for(Token token : tokenList) {
            //현재 단어
            morph = token.getMorph();

            // 글내용 중 단어가 차지하는 개수
            wordCnt = StringUtils.countOccurrencesOf(content, morph);
            map.put(morph, wordCnt);

            totalCnt += wordCnt;
        }

        List<Object> resultList = new ArrayList<>();
        resultList.add(totalCnt);
        resultList.add(map);

        return resultList;
    }
}
