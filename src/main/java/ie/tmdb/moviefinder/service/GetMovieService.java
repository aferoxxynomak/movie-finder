package ie.tmdb.moviefinder.service;

import ie.tmdb.moviefinder.dto.GetMovieDto;
import org.springframework.web.client.RestClientException;

import java.io.IOException;

public interface GetMovieService {
    void optionSearchRelated();
    GetMovieDto getMovieInfoById(String id, boolean withSimilar) throws IOException, RestClientException;
}
