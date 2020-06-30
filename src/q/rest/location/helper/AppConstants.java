package q.rest.location.helper;

public class AppConstants {

    public final static String INTERNAL_APP_SECRET = "INTERNAL_APP";

    private static final String USER_SERVICE = SysProps.getValue("userService");
    private static final String CUSTOMER_SERVICE = SysProps.getValue("customerService");

    public static final String CUSTOMER_MATCH_TOKEN = CUSTOMER_SERVICE + "match-token";
    public static final String USER_MATCH_TOKEN = USER_SERVICE + "match-token";

}
