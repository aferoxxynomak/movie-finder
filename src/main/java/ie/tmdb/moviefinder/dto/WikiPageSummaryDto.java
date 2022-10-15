package ie.tmdb.moviefinder.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class WikiPageSummaryDto {
    private String title;
    @JsonProperty("content_urls")
    private ContentUrls contentUrls;
    private String extract;
}
