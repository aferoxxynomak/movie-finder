package ie.tmdb.moviefinder.util;

import java.io.IOException;

/**
 * GraphQL resource loader
 */
public class GraphqlSchemaReaderUtil {
    public static String getSchemaFromFileName(final String filename) throws IOException {
        return new String(
                GraphqlSchemaReaderUtil.class.getClassLoader().getResourceAsStream("graphql/" + filename + ".graphql").readAllBytes());
    }
}
