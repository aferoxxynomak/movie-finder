package ie.tmdb.moviefinder.service;

import com.github.freva.asciitable.AsciiTable;
import com.github.freva.asciitable.Column;
import com.github.freva.asciitable.HorizontalAlign;
import ie.tmdb.moviefinder.dto.GraphqlRequestBody;
import ie.tmdb.moviefinder.dto.Movie;
import ie.tmdb.moviefinder.dto.SearchMoviesDto;
import ie.tmdb.moviefinder.util.Constants;
import ie.tmdb.moviefinder.util.GraphqlSchemaReaderUtil;
import ie.tmdb.moviefinder.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

@Service
public class SearchMoviesServiceImpl implements SearchMoviesService{

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());
    @Value("${graphql.url}")
    private String graphqlUrl;

    public SearchMoviesServiceImpl(){
    }

    /**
     * 1st option - Find movie information by name
     *
     * @implSpec Show the selected option name and input message. Get the movie name. Search for the movie. Show the results.
     */
    @Override
    public void optionSearchMovie(){
        LOG.info("optionSearchMovie selected");
        showSearchMovieStr();

        final Scanner scanner = new Scanner(System.in);
        if (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            if(input.trim().length() == 0){
                Utils.soutErrorMsg(Constants.ENTER_MOVIE_NAME);
                optionSearchMovie();
            } else {
                processUserInputAndShowResults(input);
            }
        }
    }

    /**
     * Fetching and processing movie information then show the result
     *
     * @param input User input movie name
     */
    private void processUserInputAndShowResults(String input) throws RuntimeException {
        try {
            SearchMoviesDto searchMoviesDto = getSearchMovies(input);
            if(searchMoviesDto == null ||
                    searchMoviesDto.getData() == null ||
                    searchMoviesDto.getData().getSearchMovies() == null ||
                    searchMoviesDto.getData().getSearchMovies().isEmpty()){
                Utils.soutErrorMsg("Not found the '" + input + "' movie!");
            } else {
                showMovieInfoTable(searchMoviesDto.getData().getSearchMovies());
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException("Cannot read schema files.", e);
        } catch (RestClientException e){
            LOG.error(e.getMessage(), e);
            throw new RuntimeException("Movie information fetch error.", e);
        }
    }

    /**
     * Make a GraphQL REST request and get the movie data
     *
     * @implSpec Create GraphQL header, load query and input variable, do the post request then get the movie data entity
     * @param movieName The title of the movie entered by the user
     * @return The movie data entity
     * @throws IOException Exception when not find the GraphQL resource files
     * @throws RestClientException REST exception
     */
    @Override
    public SearchMoviesDto getSearchMovies(String movieName) throws IOException, RestClientException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_GRAPHQL);

        GraphqlRequestBody graphQLRequestBody = new GraphqlRequestBody();
        final String query = GraphqlSchemaReaderUtil.getSchemaFromFileName("searchMovies");
        final String variables = GraphqlSchemaReaderUtil.getSchemaFromFileName("searchMoviesVariables");
        graphQLRequestBody.setQuery(query);
        graphQLRequestBody.setVariables(variables.replace("movieName", movieName));

        LOG.info("Fetching movie information...");
        System.out.println("Fetching movie information...");
        ResponseEntity<SearchMoviesDto> response = restTemplate.postForEntity(graphqlUrl, new HttpEntity<>(graphQLRequestBody, httpHeaders), SearchMoviesDto.class);
        LOG.info("Fetching movie information... done");
        System.out.println("Fetching movie information... done");

        return response.hasBody() ? response.getBody() : null;
    }

    /**
     * Show the result movie information table
     *
     * @param movies Movies list from SearchMoviesDto
     */
    private static void showMovieInfoTable(List<Movie> movies) {
        System.out.println(AsciiTable.getTable(AsciiTable.BASIC_ASCII_NO_OUTSIDE_BORDER, movies, Arrays.asList(
                new Column().header("ID").headerAlign(HorizontalAlign.CENTER).maxWidth(10).with(movie -> Long.toString(movie.getId())),
                new Column().header("Name").headerAlign(HorizontalAlign.CENTER).maxWidth(50).with(movie -> movie.getName()),
                new Column().header("Score").headerAlign(HorizontalAlign.CENTER).with(movie -> String.format("%.01f", movie.getScore())),
                new Column().header("Genre").headerAlign(HorizontalAlign.CENTER).maxWidth(30).with(movie -> movie.getGenreStringList())
        )));
    }

    /**
     * Show the search movie option title and the request for movie name
     */
    private static void showSearchMovieStr() {
        System.out.print(
                System.lineSeparator()+
                        "Find movie information by name"+System.lineSeparator()+
                        "------------------------------"+System.lineSeparator()+
                        "Enter a movie name (then hit enter): "
        );
    }
}
