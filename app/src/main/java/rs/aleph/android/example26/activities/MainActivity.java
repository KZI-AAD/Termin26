package rs.aleph.android.example26.activities;

import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import rs.aleph.android.example26.R;
import rs.aleph.android.example26.adapters.DrawerListAdapter;
import rs.aleph.android.example26.db.DatabaseHelper;
import rs.aleph.android.example26.dialogs.AboutDialog;
import rs.aleph.android.example26.fragments.DetailFragment;
import rs.aleph.android.example26.fragments.MyListFragment;
import rs.aleph.android.example26.fragments.MyListFragment.OnProductSelectedListener;
import rs.aleph.android.example26.model.NavigationItem;
import rs.aleph.android.example26.provider.ProductContract;
import rs.aleph.android.example26.provider.model.Product;

public class MainActivity extends AppCompatActivity implements OnProductSelectedListener {

    private static final int SELECT_PICTURE = 1;

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItemFromDrawer(position);
        }
    }

    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private RelativeLayout drawerPane;
    private CharSequence drawerTitle;
    private CharSequence title;

    private ArrayList<NavigationItem> navigationItems = new ArrayList<NavigationItem>();

    private AlertDialog dialog;

    private boolean landscapeMode = false;
    private boolean listShown = false;
    private boolean detailShown = false;

    private int productId = 0;

    private DatabaseHelper databaseHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        // Draws navigation items
        navigationItems.add(new NavigationItem(getString(R.string.drawer_home), getString(R.string.drawer_home_long), R.drawable.ic_action_product));
        navigationItems.add(new NavigationItem(getString(R.string.drawer_settings), getString(R.string.drawer_Settings_long), R.drawable.ic_action_settings));
        navigationItems.add(new NavigationItem(getString(R.string.drawer_about), getString(R.string.drawer_about_long), R.drawable.ic_action_about));

        title = drawerTitle = getTitle();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerList = (ListView) findViewById(R.id.navList);

        // Populate the Navigtion Drawer with options
        drawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        DrawerListAdapter adapter = new DrawerListAdapter(this, navigationItems);

        // set a custom shadow that overlays the main content when the drawer opens
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        drawerList.setAdapter(adapter);

        // Enable ActionBar app icon to behave as action to toggle nav drawer
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_drawer);
            actionBar.setHomeButtonEnabled(true);
            actionBar.show();
        }

        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                toolbar,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(title);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(drawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        if (savedInstanceState == null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            MyListFragment myListFragment = new MyListFragment();
            ft.add(R.id.displayList, myListFragment, "List_Fragment");
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
            selectItemFromDrawer(0);
        }

        if (findViewById(R.id.displayDetail) != null) {
            landscapeMode = true;
            getFragmentManager().popBackStack();

            DetailFragment detailFragment = (DetailFragment) getFragmentManager().findFragmentById(R.id.displayDetail);
            if (detailFragment == null) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                detailFragment = new DetailFragment();
                ft.replace(R.id.displayDetail, detailFragment, "Detail_Fragment1");
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
                detailShown = true;
            }
        }

        listShown = true;
        detailShown = false;
        productId = 0;
    }

    //da bi dodali podatak u bazu, potrebno je da napravimo objekat klase
    //koji reprezentuje tabelu i popunimo podacima
    private void addItem(){
        Product product = new Product();
        product.setmName("Apple");
        product.setDescription("The apple tree is a deciduous tree in the rose family best known for its sweet, pomaceous fruit, the apple.");
        product.setRating(5.0f);
        product.setImage("apples.jpg");

        //pozovemo metodu create da bi upisali u bazu
        try {
            getDatabaseHelper().getProductDao().create(product);

            //refresh();

            //koristimo automatsko osvezanje aktivnosti tako sto ponovo pozivamo kreiranje aktivnosti
            finish();
            startActivity(getIntent());

            Toast.makeText(this, "Product inserted", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**Kada koristimo  Content provider sve sto zelimo da radimo nad bazom
     * Moramo da navedemo kroz odredjenu URI putanju
     *
     * Ako zelimo da unsemo novi product moramo koristiti insert metodu prethodno
     * definisanog URI-a
     * content://AUTHORITY/CONTENT_URI_PATH/ odnosno u nasem slucaju
     *
     * content://rs.aleph.android.example26/products
     * I moramo formirati ContentValues objekat kljuc-vrednost sta zelimo i u koju kolonu da unesemo
    */
    private void addItemWithProvider(){
        // insert test
        ContentValues values = new ContentValues();
        values.clear();
        values.put(rs.aleph.android.example26.provider.model.Product.FIELD_NAME_NAME, "apple");
        values.put(rs.aleph.android.example26.provider.model.Product.FIELD_NAME_DESCRIPTION,"The apple tree is a deciduous tree in the rose family best known for its sweet, pomaceous fruit, the apple.");
        values.put(rs.aleph.android.example26.provider.model.Product.FIELD_NAME_RATING, 5.0f);
        values.put(rs.aleph.android.example26.provider.model.Product.FIELD_NAME_IMAGE, "apples.jpg");
        getContentResolver().insert(ProductContract.CONTENT_URI, values);

        Toast.makeText(this, "Inserted", Toast.LENGTH_SHORT).show();

        /**
         * Jednostavan test kako proci kroz celu tabelu products koja se nalazi na URI:
         *
         *  content://rs.aleph.android.example26/products
         *
         *  Prvi parametar kursora je projekcija tj sta zelimo da selektujemo iz tabele
         *  Drugi parametar je selekcioni naziv praktcno WHERE u klasicnom SQL-u tj po kojim kolonama zelimo da filtriramo
         *  Treci paramerar su vrednosti tih selekcionih argumenta
         *  Peti je po cemu zelimo da sortiramo Cursor
         */
        Cursor c = getContentResolver().query(ProductContract.CONTENT_URI, null, null, null, null);
        c.moveToFirst();
        do {
            for (int i = 0; i < c.getColumnCount(); i++) {
                Log.d(getClass().getSimpleName(), c.getColumnName(i) + " : " + c.getString(i));
            }
        } while (c.moveToNext());
        c.close();

        finish();
        startActivity(getIntent());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_item_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
    * Da bi dobili pristup Galeriji slika na uredjaju
    * moramo preko URI-ja pristupiti delu baze gde su smestene
    * slike uredjaja. Njima mozemo pristupiti koristeci sistemski
    * ContentProvider i koristeci URI images/* putanju
    *
    * Posto biramo sliku potrebno je da pozovemo aktivnost koja icekuje rezultat
    * Kada dobijemo rezultat nazad prikazemo sliku i dobijemo njenu tacnu putanju
    * */
    private void selectPicture(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    /**
     * Sismtemska metoda koja se automatksi poziva ako se
     * aktivnost startuje u startActivityForResult rezimu
     *
     * Ako je ti slucaj i ako je sve proslo ok, mozemo da izvucemo
     * sadrzaj i to da prikazemo. Rezultat NIJE sliak nego URI do te slike.
     * Na osnovu toga mozemo dobiti tacnu putnaju do slike ali i samu sliku
     * */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();

                Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.image_dialog);
                dialog.setTitle("Image dialog");

                ImageView image = (ImageView) dialog.findViewById(R.id.image);
                TextView textview = (TextView) dialog.findViewById(R.id.image_path);

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    image.setImageBitmap(bitmap);
                    textview.setText(selectedImageUri.getPath());

                    dialog.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refresh();
                break;
            case R.id.action_add:
                //addItem();
                addItemWithProvider();
                break;
            case R.id.action_photo:
                selectPicture();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    // refresh() prikazuje novi sadrzaj.Povucemo nov sadrzaj iz baze i popunimo listu
    private void refresh() {
        //ListView listview = (ListView) findViewById(R.id.products);

//        if (listview != null){
//            ArrayAdapter<Product> adapter = (ArrayAdapter<Product>) listview.getAdapter();
//
//            if(adapter!= null)
//            {
//                try {
//                    adapter.clear();
//                    List<Product> list = getDatabaseHelper().getProductDao().queryForAll();
//
//                    adapter.addAll(list);
//
//                    adapter.notifyDataSetChanged();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }

    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void selectItemFromDrawer(int position) {
        if (position == 0){

        } else if (position == 1){
            Intent settings = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(settings);
        } else if (position == 2){
            if (dialog == null){
                dialog = new AboutDialog(MainActivity.this).prepareDialog();
            } else {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }

            dialog.show();
        }

       drawerList.setItemChecked(position, true);
       setTitle(navigationItems.get(position).getTitle());
       drawerLayout.closeDrawer(drawerPane);
    }

    @Override
    public void onProductSelected(int id) {

        productId = id;

        try {
            Product product = getDatabaseHelper().getProductDao().queryForId(id);

            if (landscapeMode) {
                DetailFragment detailFragment = (DetailFragment) getFragmentManager().findFragmentById(R.id.displayDetail);
                detailFragment.updateProduct(product);
            } else {
                    DetailFragment detailFragment = new DetailFragment();
                    detailFragment.setProduct(product);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.displayList, detailFragment, "Detail_Fragment2");
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.addToBackStack("Detail_Fragment2");
                    ft.commit();
                    listShown = false;
                    detailShown = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

        if (landscapeMode) {
            finish();
        } else if (listShown == true) {
            finish();
        } else if (detailShown == true) {
            getFragmentManager().popBackStack();
            MyListFragment myListFragment = new MyListFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.displayList, myListFragment, "List_Fragment");
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
            listShown = true;
            detailShown = false;
        }

    }

    //Metoda koja komunicira sa bazom podataka
    public DatabaseHelper getDatabaseHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // nakon rada sa bazo podataka potrebno je obavezno
        //osloboditi resurse!
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }
}


