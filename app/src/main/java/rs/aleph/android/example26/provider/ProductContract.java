package rs.aleph.android.example26.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by milossimic on 11/7/16.
 */
public class ProductContract implements BaseColumns{

    public ProductContract() {
    }

    public static final String TABLE_NAME = "products";

    public static final String AUTHORITY = "rs.aleph.android.example26";

    public static final String CONTENT_URI_PATH = "products";

    public static final String MIMETYPE_TYPE = "products";
    public static final String MIMETYPE_NAME = "rs.aleph.android.example26.provider";

    public static final int CONTENT_URI_PATTERN_MANY = 1;
    public static final int CONTENT_URI_PATTERN_ONE = 2;

    public static final Uri CONTENT_URI = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(AUTHORITY).appendPath(CONTENT_URI_PATH).build();
}
