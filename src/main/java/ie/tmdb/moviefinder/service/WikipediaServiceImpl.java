package ie.tmdb.moviefinder.service;

import ie.tmdb.moviefinder.dto.GetMovieDto;
import ie.tmdb.moviefinder.dto.WikiPageSummaryDto;
import ie.tmdb.moviefinder.util.Constants;
import ie.tmdb.moviefinder.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

@Service
public class WikipediaServiceImpl implements WikipediaService{

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());
    @Value("${wiki.url}")
    private String wikiUrl;

    private GetMovieService getMovieService;

    public WikipediaServiceImpl(GetMovieService getMovieService) {
        this.getMovieService = getMovieService;
    }

    /**
     * 2nd - Find Wikipedia details for a movie by ID
     *
     * @implSpec Show the selected option name and input message. Get the movie ID. Search for the movie name then search it in Wikipedia. Show the results.
     */
    @Override
    public void optionSearchWiki(){
        LOG.info("optionSearchWiki selected");
        showSearchWikiStr();

        final Scanner scanner = new Scanner(System.in);
        if (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            if(input.trim().length() == 0){
                Utils.soutErrorMsg(Constants.ENTER_MOVIE_ID);
                optionSearchWiki();
            } else {
                try{
                    if(Integer.parseUnsignedInt(input) > 0){
                        processUserInputAndShowResults(input);
                    }
                } catch (NumberFormatException e){
                    Utils.soutErrorMsg(Constants.NOT_VALID_ID);
                    optionSearchWiki();
                }
            }
        }
    }

    /**
     * Process the user input (movie ID), search for the movie name then search it in Wikipedia. Show the results.
     *
     * @param input Movie ID
     * @throws RuntimeException Exception to be processed in the main loop
     */
    private void processUserInputAndShowResults(String input) throws RuntimeException {
        try{
            GetMovieDto getMovieDto = getMovieService.getMovieInfoById(input, false);
            if(getMovieDto == null ||
                    getMovieDto.getData() == null ||
                    getMovieDto.getData().getMovie() == null ||
                    getMovieDto.getData().getMovie().getName() == null ||
                    getMovieDto.getData().getMovie().getName().isEmpty()){
                Utils.soutErrorMsg("Not found the movie by the ID '" + input + "'!");
            } else {
                String movieName = getMovieDto.getData().getMovie().getName();
                System.out.println("The movie '" + movieName + "' found by '" + input + "' ID.");
                movieName = Utils.replaceSpaceWithUnderscore(movieName);

                WikiPageSummaryDto wikiPageSummaryDto = getWikipediaInformation(movieName);
                if(wikiPageSummaryDto == null){
                    System.out.println("Not found the Wikipedia page of the '" + movieName + "' movie.");
                } else {
                    System.out.println("Movie name: " + wikiPageSummaryDto.getTitle());
                    System.out.println("Summary: " + wikiPageSummaryDto.getExtract());
                    System.out.println("Wikipedia URL: " + wikiPageSummaryDto.getContentUrls().getDesktop().getPage());
                }
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
     * Fetch Wikipedia information by movie name
     * @param movieName Movie name
     * @return Wikipedia entity
     * @throws RestClientException REST exception
     */
    private WikiPageSummaryDto getWikipediaInformation(String movieName) throws RestClientException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = createHttpHeaders();

        System.out.println("Fetching Wikipedia information...");
        ResponseEntity<WikiPageSummaryDto> response = restTemplate.exchange(wikiUrl + movieName, HttpMethod.GET, new HttpEntity<>(httpHeaders), WikiPageSummaryDto.class);
        System.out.println("Fetching Wikipedia information... done");

        return response.hasBody() ? response.getBody() : null;
    }

    /**
     * Create HTTP headers for REST call
     * @return HTTP header object
     */
    private static HttpHeaders createHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();

        List<Charset> charsets = new ArrayList<>(1);
        charsets.add(StandardCharsets.UTF_8);
        httpHeaders.setAcceptCharset(charsets);

        List<Locale> languages = new ArrayList<>(1);
        languages.add(Locale.ENGLISH);
        httpHeaders.setAcceptLanguageAsLocales(languages);

        List<MediaType> mediaTypes = new ArrayList<>(1);
        mediaTypes.add(MediaType.APPLICATION_GRAPHQL);
        httpHeaders.setAccept(mediaTypes);

        httpHeaders.set("profile", "https://www.mediawiki.org/wiki/Specs/Summary/1.4.2");
        return httpHeaders;
    }

    /**
     * Show the search movie option title and the request for movie name
     */
    private static void showSearchWikiStr() {
        System.out.print(
                System.lineSeparator()+
                        "Find Wikipedia details for a movie by ID"+System.lineSeparator()+
                        "----------------------------------------"+System.lineSeparator()+
                        "Enter a movie ID (then hit enter): "
        );
    }

}
