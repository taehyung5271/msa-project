package com.example.boardservice.service;

import com.example.boardservice.client.UserClient;
import com.example.boardservice.domain.Post;
import com.example.boardservice.dto.PostRequestDto;
import com.example.boardservice.dto.PostResponseDto;
import com.example.boardservice.dto.UserDto;
import com.example.boardservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserClient userClient;
    // 게시글 작성
    @Transactional
    public PostResponseDto createPost(PostRequestDto request) {
        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .userId(request.getUserId())
                .build();

        Post saved = postRepository.save(post);

        //작성자 정보 가져오기(오픈페인에서!)
        UserDto user = userClient.getUser(saved.getUserId());

        return PostResponseDto.from(saved, user);
    }

    // 게시글 목록 조회
    public List<PostResponseDto> getAllPosts() {
        List<Post> posts = postRepository.findAll();

        return posts.stream()
                .map(post -> {
                    UserDto user = userClient.getUser(post.getUserId());
                    return PostResponseDto.from(post,user);
                })
                .collect(Collectors.toList());
    }

    // 게시글 상세 조회
    public PostResponseDto getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        UserDto user = userClient.getUser(post.getUserId());
        return PostResponseDto.from(post,user);
    }

    // 게시글 수정
    @Transactional
    public PostResponseDto updatePost(Long id, PostRequestDto request) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        post.update(request.getTitle(), request.getContent());

        UserDto user = userClient.getUser(post.getUserId());

        return PostResponseDto.from(post,user);
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        postRepository.delete(post);
    }
}