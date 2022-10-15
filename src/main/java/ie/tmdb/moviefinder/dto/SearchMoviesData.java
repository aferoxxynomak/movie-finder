package ie.tmdb.moviefinder.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchMoviesData {
    private List<Movie> searchMovies;
}
