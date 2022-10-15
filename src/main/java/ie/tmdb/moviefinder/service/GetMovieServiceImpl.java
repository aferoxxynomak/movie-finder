package ie.tmdb.moviefinder.service;

import com.github.freva.asciitable.AsciiTable;
import com.github.freva.asciitable.Column;
import com.github.freva.asciitable.HorizontalAlign;
import ie.tmdb.moviefinder.dto.GetMovieDto;
import ie.tmdb.moviefinder.dto.GraphqlRequestBody;
import ie.tmdb.moviefinder.dto.Movie;
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
public class GetMovieServiceImpl implements GetMovieService{

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());
    @Value("${graphql.url}")
    private String graphqlUrl;

    public GetMovieServiceImpl() {
    }

    /**
     * 3rd - Find related movies by ID
     *
     * @implSpec Show the selected option name and input message. Get the movie ID. Search for related movies. Show the results.
     */
    @Override
    public void optionSearchRelated(){
        LOG.info("optionSearchRelated selected");
        showSearchRelatedStr();

        final Scanner scanner = new Scanner(System.in);
        if (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            if(input.trim().length() == 0){
                Utils.soutErrorMsg(Constants.ENTER_MOVIE_ID);
                optionSearchRelated();
            } else {
                try{
                    if(Integer.parseUnsignedInt(input) > 0){
                        processUserInputAndShowResults(input);
                    }
                } catch (NumberFormatException e){
                    Utils.soutErrorMsg(Constants.NOT_VALID_ID);
                    optionSearchRelated();
                }
            }
        }
    }

    /**
     * Process the user input (movie ID), search for related movies. Show the results.
     *
      * @param input Movie ID
     * @throws RuntimeException Exception to be processed in the main loop
     */
    private void processUserInputAndShowResults(String input) throws RuntimeException {
        try{
            GetMovieDto getMovieDto = getMovieInfoById(input, true);
            if(getMovieDto == null ||
                    getMovieDto.getData() == null ||
                    getMovieDto.getData().getMovie() == null) {
                Utils.soutErrorMsg("Not found the movie by the ID '" + input + "'!");
            } else if(getMovieDto.getData().getMovie().getSimilar() == null ||
                    getMovieDto.getData().getMovie().getSimilar().isEmpty()){
                Utils.soutErrorMsg("There is not any related movies by the ID '" + input + "'!");
            } else {
                System.out.println("The movie '" + getMovieDto.getData().getMovie().getName() + "' found by the ID '" + input + "'.");
                showRelatedMoviesTable(getMovieDto);
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException("Cannot read schema files.", e);
        } catch (RestClientException e){
            LOG.error(e.getMessage(), e);
            throw new RuntimeException("Movie information fecthing error.", e);
        }
    }

    /**
     * Show the result related movie information table
     *
     * @param getMovieDto Movies list from getMovieDto
     */
    private static void showRelatedMoviesTable(GetMovieDto getMovieDto) {
        List<Movie> movies = getMovieDto.getData().getMovie().getSimilar();
        System.out.println(AsciiTable.getTable(AsciiTable.BASIC_ASCII_NO_OUTSIDE_BORDER, movies, Arrays.asList(
                new Column().header("ID").headerAlign(HorizontalAlign.CENTER).maxWidth(10).with(movie -> Long.toString(movie.getId())),
                new Column().header("Name").headerAlign(HorizontalAlign.CENTER).maxWidth(50).with(movie -> movie.getName())
        )));
    }

    /**
     * Fetch movie information by ID
     *
     * @param movieId Movie ID
     * @param withSimilar Find with similar list
     * @return Movie entity
     * @throws IOException Exception when not find the GraphQL resource files
     * @throws RestClientException REST exception
     */
    @Override
    public GetMovieDto getMovieInfoById(String movieId, boolean withSimilar) throws IOException, RestClientException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_GRAPHQL);

        GraphqlRequestBody graphQLRequestBody = new GraphqlRequestBody();
        final String query = GraphqlSchemaReaderUtil.getSchemaFromFileName((withSimilar ? "getMovieWithSimilar" : "getMovie"));
        final String variables = GraphqlSchemaReaderUtil.getSchemaFromFileName("getMovieVariable");
        graphQLRequestBody.setQuery(query);
        graphQLRequestBody.setVariables(variables.replace("movieId", movieId));

        System.out.println("Fetching movie information...");
        ResponseEntity<GetMovieDto> response = restTemplate.postForEntity(graphqlUrl, new HttpEntity<>(graphQLRequestBody, httpHeaders), GetMovieDto.class);
        System.out.println("Fetching movie information... done");

        return response.hasBody() ? response.getBody() : null;
    }

    /**
     * Show the search related option title and the request for movie ID
     */
    private static void showSearchRelatedStr() {
        System.out.print(
                System.lineSeparator()+
                        "Find related movies by ID"+System.lineSeparator()+
                        "-------------------------"+System.lineSeparator()+
                        "Enter a movie ID (then hit enter): "
        );
    }

}
