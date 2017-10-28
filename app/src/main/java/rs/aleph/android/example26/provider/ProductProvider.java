package rs.aleph.android.example26.provider;

import com.tojc.ormlite.android.OrmLiteSimpleContentProvider;
import com.tojc.ormlite.android.framework.MatcherController;
import com.tojc.ormlite.android.framework.MimeTypeVnd;

import rs.aleph.android.example26.db.DatabaseHelper;
import rs.aleph.android.example26.provider.model.Product;

/**
 * Created by milossimic on 11/8/16.
 */
/**
 * Da bi mogli da koristimo ORMLight maper, ne mozemo da koristimo
 * Podrazumevani ContentProvider. Za to ce nam pomoci Android-OrmLiteContentProvider
 * bibliteka
 *
 * ContentProvider mora da nasldi OrmLiteSimpleContentProvider i da se parametrizuje sa
 * nasom bazom
 *
 * U metodi onCreate potrebno je da kazemo koji to URI selektuje sve elemente iz tabele
 * odnosno po odredjenom id-u
 *
 * content://rs.aleph.android.example26/products selektuje sve
 * content://rs.aleph.android.example26/products/1 selektuje product sa id-om 1
 * */
public class ProductProvider extends OrmLiteSimpleContentProvider<DatabaseHelper> {
    @Override
    protected Class<DatabaseHelper> getHelperClass() {
        return DatabaseHelper.class;
    }

    @Override
    public boolean onCreate() {
        setMatcherController(new MatcherController()//
                .add(Product.class, MimeTypeVnd.SubType.DIRECTORY, "", ProductContract.Product.CONTENT_URI_PATTERN_MANY)//
                .add(Product.class, MimeTypeVnd.SubType.ITEM, "#", ProductContract.Product.CONTENT_URI_PATTERN_ONE));
        return true;
    }
}
