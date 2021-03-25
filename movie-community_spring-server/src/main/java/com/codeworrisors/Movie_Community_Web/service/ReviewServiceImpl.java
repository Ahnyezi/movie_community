package com.codeworrisors.Movie_Community_Web.service;

import com.codeworrisors.Movie_Community_Web.constant.NaverApiProperties;
import com.codeworrisors.Movie_Community_Web.dto.ReviewDTO;
import com.codeworrisors.Movie_Community_Web.model.Image;
import com.codeworrisors.Movie_Community_Web.model.Likes;
import com.codeworrisors.Movie_Community_Web.model.Member;
import com.codeworrisors.Movie_Community_Web.model.Review;
import com.codeworrisors.Movie_Community_Web.repository.ImageRepository;
import com.codeworrisors.Movie_Community_Web.repository.ReviewRepository;
import com.codeworrisors.Movie_Community_Web.request.ReviewRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ImageRepository imageRepository;
    private Member currentMember;

    public ReviewServiceImpl(ReviewRepository reviewRepository, ImageRepository imageRepository) {
        this.reviewRepository = reviewRepository;
        this.imageRepository = imageRepository;
    }

    /*
    * Naver 영화 API 통신
    * */
    @Override
    public String searchMovie(String title) throws IOException {
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", NaverApiProperties.CLIENT_ID);
        requestHeaders.put("X-Naver-Client-Secret", NaverApiProperties.CLIENT_SECRET);
        return get(NaverApiProperties.API_URL + title, requestHeaders);
    }

    private String get(String apiUrl, Map<String, String> requestHeaders) throws IOException {
        // 요청
        HttpURLConnection conn = connect(apiUrl);
        try {
            conn.setRequestMethod("GET");
            for (Map.Entry<String, String> header : requestHeaders.entrySet()) {
                conn.setRequestProperty(header.getKey(), header.getValue());
            }

            // 응답
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(conn.getInputStream());
            } else { // 에러 발생
                return readBody(conn.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            conn.disconnect();
        }
    }

    private HttpURLConnection connect(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    private String readBody(InputStream body) {
        InputStreamReader streamReader = new InputStreamReader(body);

        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();

            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }

            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }

    @Override
    public Image createImage(Image image) {
        return imageRepository.save(image);
    }

    @Override
    public Review createReview(Review review) {
        return reviewRepository.save(review);
    }

    @Override
    public Review updateReview(Review review) {
        if (reviewRepository.findById(review.getId()).isEmpty())
            return null;
        return reviewRepository.save(review);
    }

    @Override
    public void deleteReview(Review review) {
        reviewRepository.delete(review);
    }

    public void clear() {
        reviewRepository.deleteAll();
    }

    public Review getReviewById(long reviewId) {
        return reviewRepository.findById(reviewId).get();
    }


    @Override
    public Page<ReviewDTO> getReviewData(int page, int size, Member currentMember, ReviewDTO reviewFilter) {
        this.currentMember = currentMember;
        Page<Review> reviewEntity = null;
        Page<ReviewDTO> reviewDTOs = null;
        if( reviewFilter.getMovieTitle() != null ){
            reviewEntity = reviewRepository.findAllByMovieTitle(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createDate")),reviewFilter.getMovieTitle());
            reviewDTOs = reviewEntity.map(this::convertReviewEntityToReviewDTO);
            return reviewDTOs;
        }

        if( reviewFilter.getWriterName() != null ){
            Member member = new Member();
            member.setId( currentMember.getId() );
            reviewEntity = reviewRepository.findAllByMember(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createDate")),member);
            reviewDTOs = reviewEntity.map(this::convertReviewEntityToReviewDTO);
            return reviewDTOs;
        }

        reviewEntity = reviewRepository
        .findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createDate")));
        reviewDTOs = reviewEntity.map(this::convertReviewEntityToReviewDTO);
        return reviewDTOs;
    }

    private ReviewDTO convertReviewEntityToReviewDTO(final Review review) {
        final ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setReviewId(review.getId());
        reviewDTO.setWriterName(review.getMember().getMemberName());
        reviewDTO.setCreateDate(review.getCreateDate());
        reviewDTO.setMovieTitle(review.getMovieTitle());
        reviewDTO.setContent(review.getContent());
        reviewDTO.setImages(review.getImages());
        reviewDTO.setRating(review.getRating());
        reviewDTO.setLikes(review.getLikes().size());
        reviewDTO.setComments(review.getComments());
        List<Likes> likes = review.getLikes();

        for (Likes like : likes) {
            if (like.getMember().getId() == currentMember.getId()) {
                reviewDTO.setLikePressed(true);
                return reviewDTO;
            }
        }

        return reviewDTO;
    }
}
