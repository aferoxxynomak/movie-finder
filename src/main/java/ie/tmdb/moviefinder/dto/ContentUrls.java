package ie.tmdb.moviefinder.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContentUrls {
    private ContentUrl desktop;
}
