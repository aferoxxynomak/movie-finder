package ie.tmdb.moviefinder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Application startup point
 * for the Movie Finder
 *
 * @author Zoltan Benya
 * @version 1.0 (2022.10)
 * @url https://github.com/aferoxxynomak/movie-finder
 * @implNote The .close() is for exit the application, when the command line is completed.
 */
@SpringBootApplication
public class MovieFinderApplication {

    private static Logger LOG = LoggerFactory.getLogger(MovieFinderApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(MovieFinderApplication.class, args).close();
    }

}
