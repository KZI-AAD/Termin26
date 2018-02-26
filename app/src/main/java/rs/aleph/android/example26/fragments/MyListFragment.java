package rs.aleph.android.example26.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.CloseableIterator;

import java.sql.SQLException;
import java.util.List;

import rs.aleph.android.example26.R;
import rs.aleph.android.example26.activities.MainActivity;
import rs.aleph.android.example26.db.DatabaseHelper;
import rs.aleph.android.example26.provider.ProductContract;
import rs.aleph.android.example26.provider.model.Product;

/**
* Kada zelimo automatsko osvezanja liste moramo koristii ListFragment
* **/
public class MyListFragment extends ListFragment {

    private Cursor c;
    private MainActivity activity;

    //potrebno je dodati i jedan interato koji ce automatsi osvezavati prikaz
    private CloseableIterator<Product> iterator;

    // Container Activity must implement this interface
    public interface OnProductSelectedListener {
        void onProductSelected(int id);
    }

    OnProductSelectedListener listener;
    ListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=((MainActivity)getActivity());
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        /**
         * Na svaki lik, uzimamo kursor adaptera i trazimo _id elmenta
         * sa pozicije koji smo kliknuli.
         *
         * NAPOMENA: biblioteka zahteva postojanje _id elementa _id nije isto sto i id!
         * */
        Cursor cur = (Cursor) adapter.getItem(position);
        cur.moveToPosition(position);
        String cID = cur.getString(cur.getColumnIndexOrThrow("_id"));

        //Kada dobijemo kljuc, pretvorimo ga u Integer i izazivamo klik doagadjaj
        listener.onProductSelected(Integer.parseInt(cID));
    }

    @Override
    public void onResume() {
        super.onResume();
        c = activity.getContentResolver().query(ProductContract.Product.contentUri, null, null, null, null);
        if (c != null) {

            //iz kursora izvlacimo kolone baze koji nas zanimaju
            String[] from = new String[] { ProductContract.Product.FIELD_NAME_NAME, ProductContract.Product.FIELD_NAME_DESCRIPTION };

            //i smestamo u nas layout na poziciju gde zelimo, pozicije su opisane id-om elemenata
            int[] to = new int[] {R.id.name, R.id.description};

            //inicijalizujemo adapter da se sam osvezava
            adapter = new SimpleCursorAdapter(activity, R.layout.cinema_list, c, from, to, 0);

            //ListFrafgment vec u sebi ima listu sto znaci daje potrebno da samo dodamo adapter
            setListAdapter(adapter);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (c != null) {
            c.close();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        View view = inflater.inflate(R.layout.list_fragment, container, false);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
           listener = (OnProductSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnProductSelectedListener");
        }
    }
}
