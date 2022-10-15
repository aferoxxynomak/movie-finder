package ie.tmdb.moviefinder.component;

import ie.tmdb.moviefinder.service.GetMovieService;
import ie.tmdb.moviefinder.service.SearchMoviesService;
import ie.tmdb.moviefinder.service.WikipediaService;
import ie.tmdb.moviefinder.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.repeat.support.RepeatTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

/**
 * The main command line runner
 * User interaction manager
 */
@Component
public class CommandLineAppStartupRunner implements CommandLineRunner {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final SearchMoviesService searchMoviesService;
    private final GetMovieService getMovieService;
    private final WikipediaService wikipediaService;

    /**
     * Constructor with service wire
     * @param searchMoviesService
     * @param getMovieService
     * @param wikipediaService
     */
    public CommandLineAppStartupRunner(SearchMoviesService searchMoviesService, GetMovieService getMovieService, WikipediaService wikipediaService) {
        this.searchMoviesService = searchMoviesService;
        this.getMovieService = getMovieService;
        this.wikipediaService = wikipediaService;
    }

    /**
     * Main commandline executor
     * Main loop with RepeatTemplate and RepeatCallback (replaced with lambda)
     *
     * @param args No application argument
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        LOG.info("Application CommandLineRunner Started");
        RepeatTemplate template = new RepeatTemplate();
        template.iterate(context -> mainLoop());
        LOG.info("Application CommandLineRunner Ended");
    }

    /**
     * The main loop string and option selector
     *
     * @return RepeatStatus for re-show the menu or exit the application
     */
    private RepeatStatus mainLoop() {
        showMainMenuStr();
        return mainMenuUserOptionSelector();
    }

    /**
     * Show main menu title and options
     */
    private static void showMainMenuStr() {
        System.out.print(
                "Movie Finder"+System.lineSeparator()+
                        "------------"+System.lineSeparator()+
                        "1. Find movie information by name"+System.lineSeparator()+
                        "2. Find Wikipedia details for a movie by ID"+System.lineSeparator()+
                        "3. Find related movies by ID"+System.lineSeparator()+
                        "4. Exit"+System.lineSeparator()+
                        "Choose an option (enter the number and hit enter): "
        );
    }

    /**
     * Main loop option selector by the user
     *
     * @return RepeatStatus for re-show the menu or exit the application
     */
    private RepeatStatus mainMenuUserOptionSelector() {
        final Scanner scanner = new Scanner(System.in);

        if (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            LOG.info("User menu input: " + input);
            try {
                switch (input) {
                    case "1" -> searchMoviesService.optionSearchMovie();
                    case "2" -> wikipediaService.optionSearchWiki();
                    case "3" -> getMovieService.optionSearchRelated();
                    case "4" -> {
                        System.out.println("Exiting...");
                        return RepeatStatus.FINISHED;
                    }
                    default -> Utils.soutErrorMsg("Choose a valid option!");
                }
            } catch (RuntimeException e){
                Utils.soutErrorMsg(e.getMessage());
            } catch (Exception e){
                Utils.soutErrorMsg("Unknown error");
            }
        }

        System.out.println();
        return RepeatStatus.CONTINUABLE;
    }
}
