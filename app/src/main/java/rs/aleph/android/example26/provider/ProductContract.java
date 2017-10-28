package rs.aleph.android.example26.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by milossimic on 11/7/16.
 */
public class ProductContract{

    public static final String DATABASE_NAME = "MyDatabase";
    public static final int DATABASE_VERSION = 1;

    public static final String AUTHORITY = "rs.aleph.android.example26";

    public static class Product implements BaseColumns
    {
        public static final String TABLE_NAME = "products";

        public static final String CONTENT_URI_PATH = TABLE_NAME;

        public static final String MIMETYPE_TYPE = TABLE_NAME;
        public static final String MIMETYPE_NAME = AUTHORITY + ".provider";

        public static final String FIELD_NAME_NAME = "name";
        public static final String FIELD_NAME_DESCRIPTION = "description";
        public static final String FIELD_NAME_RATING = "rating";
        public static final String FIELD_NAME_IMAGE = "image";

        public static final int CONTENT_URI_PATTERN_MANY = 1;
        public static final int CONTENT_URI_PATTERN_ONE = 2;

        public static final Uri contentUri = new Uri.Builder()
                .scheme(ContentResolver.SCHEME_CONTENT)
                .authority(AUTHORITY)
                .appendPath(CONTENT_URI_PATH).build();
    }
}
