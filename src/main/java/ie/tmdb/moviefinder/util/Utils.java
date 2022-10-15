package ie.tmdb.moviefinder.util;

/**
 * Utilities, helping functions
 */
public abstract class Utils {

    /**
     * Display colored error message to SysOut
     *
     * @param msg Error message
     */
    public static void soutErrorMsg(String msg){
        System.out.println(Constants.COLOR_RED + "- " + msg + " -" + Constants.COLOR_RESET);
    }

    /**
     * Replace space characters with underscore
     * @param str Input string
     * @return Replaced string
     */
    public static String replaceSpaceWithUnderscore(String str){
        return str.replaceAll(" ", "_");
    }
}
