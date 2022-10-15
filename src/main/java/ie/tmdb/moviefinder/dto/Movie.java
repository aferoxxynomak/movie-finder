package ie.tmdb.moviefinder.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Movie {
    private Long id;
    private String name;
    private Double score;
    private List<Genre> genres;
    private List<Movie> similar;

    public String getGenreStringList(){
        if(genres == null){
            return "";
        }
        return genres.stream().map(genre -> String.valueOf(genre.getName())).collect(Collectors.joining(","));
    }
}
