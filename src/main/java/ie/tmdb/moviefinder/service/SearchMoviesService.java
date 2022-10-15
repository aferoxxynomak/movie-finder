package ie.tmdb.moviefinder.service;

import ie.tmdb.moviefinder.dto.SearchMoviesDto;
import org.springframework.web.client.RestClientException;

import java.io.IOException;

public interface SearchMoviesService {
    void optionSearchMovie();
    SearchMoviesDto getSearchMovies(final String movieName) throws IOException, RestClientException;
}
