package rs.aleph.android.example26.provider.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation;

import rs.aleph.android.example26.provider.ProductContract;

/**
 * Created by milossimic on 11/7/16.
 */

//Kao i do sada moramo mapirati naziv tabele da bi znali kako da je smetimo u bazu
@DatabaseTable(tableName = ProductContract.Product.TABLE_NAME)
//Moramo dodati i content URI da bi znali gde se tacno nasa tabela nalazi content://AUTHORITY/products
@AdditionalAnnotation.DefaultContentUri(authority = ProductContract.AUTHORITY, path = ProductContract.Product.CONTENT_URI_PATH)
//Takodje moramo mapirati i MIMETYPE_NAME da bi znali na koje sve tipove nasa tabela moze da reaguje tekst,slike,nasa tabela itd
@AdditionalAnnotation.DefaultContentMimeTypeVnd(name = ProductContract.Product.MIMETYPE_NAME, type = ProductContract.Product.MIMETYPE_TYPE)
public class Product {

    @DatabaseField(columnName = ProductContract.Product._ID, generatedId = true)
    @AdditionalAnnotation.DefaultSortOrder
    private int mId;

    @DatabaseField(columnName = ProductContract.Product.FIELD_NAME_NAME)
    private String mName;

    @DatabaseField(columnName = ProductContract.Product.FIELD_NAME_DESCRIPTION)
    private String description;

    @DatabaseField(columnName = ProductContract.Product.FIELD_NAME_RATING)
    private float rating;

    @DatabaseField(columnName = ProductContract.Product.FIELD_NAME_IMAGE)
    private String image;

    //ORMLite zahteva prazan konstuktur u klasama koje opisuju tabele u bazi!
    public Product() {
    }

    /** Getters & Setters **/

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return  mName;
    }
}
