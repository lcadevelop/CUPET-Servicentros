package cu.tecnomatica.android.cupet_servicentros.activities.mapa;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.greenrobot.greendao.database.Database;
import java.io.File;
import java.util.List;
import cu.tecnomatica.android.cupet_servicentros.R;
import cu.tecnomatica.android.cupet_servicentros.activities.ayuda.AyudaActivity;
import cu.tecnomatica.android.cupet_servicentros.activities.ayuda.CreditosActivity;
import cu.tecnomatica.android.cupet_servicentros.database.Combustible;
import cu.tecnomatica.android.cupet_servicentros.database.DaoMaster;
import cu.tecnomatica.android.cupet_servicentros.database.DaoSession;
import cu.tecnomatica.android.cupet_servicentros.database.Provincia;

public class MapaActivity extends AppCompatActivity
{
    FragmentManager fragmentManager = getSupportFragmentManager();
    MapaFragment mapaFragment = new MapaFragment();

    private static final String DB_FILE = "/CUPET/servi.db";

    List<Provincia> provincias;
    List<Combustible> combustibles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        fragmentManager.beginTransaction().replace(R.id.id_fragment_contenedor_mapa, mapaFragment).commit();

        BottomNavigationView menu_navigation = (BottomNavigationView)findViewById(R.id.id_bottom_navigation);
        menu_navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
            {
                switch (menuItem.getItemId())
                {
                    case R.id.id_navigation_menu_combustibles:
                        SeleccionarCombustible();
                        return true;

                    case R.id.id_navigation_menu_provincias:
                        SeleccionarProvincia();
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principal, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings)
        {
            //Intent intent = new Intent(MapaActivity.this, SettingsActivity.class);
            //startActivity(intent);
            return true;
        }*/
        if (id == R.id.action_help)
        {
            Intent intent = new Intent(MapaActivity.this, AyudaActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_creditos)
        {
            Intent intent = new Intent(MapaActivity.this, CreditosActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void SeleccionarProvincia()
    {
        String dbPath = new File(Environment.getExternalStorageDirectory().getPath() + DB_FILE).getAbsolutePath();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, dbPath);
        Database database = helper.getWritableDb();
        final DaoSession daoSession = new DaoMaster(database).newSession();

        provincias = daoSession.getProvinciaDao().loadAll();

        AlertDialog.Builder listaprovincias = new AlertDialog.Builder(this);
        listaprovincias.setTitle("Seleccione la Provincia");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item);

        for (int i = 0; i < provincias.size(); i++)
        {
            arrayAdapter.add(provincias.get(i).getNombre());
        }

        listaprovincias.setAdapter(arrayAdapter, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String provinciaseleccionada = arrayAdapter.getItem(which);

                for (int i = 0; i < provincias.size(); i++)
                {
                    Provincia provinciaTemporal = provincias.get(i);
                    if (provinciaTemporal.getNombre().equals(provinciaseleccionada))
                    {
                        provinciaTemporal.setActiva(true);
                        daoSession.insertOrReplace(provinciaTemporal);
                    }
                    else
                    {
                        provinciaTemporal.setActiva(false);
                        daoSession.insertOrReplace(provinciaTemporal);
                    }
                }

                Toast toast = Toast.makeText(getApplicationContext(), "Provincia Seleccionada: " + provinciaseleccionada, Toast.LENGTH_SHORT);
                toast.show();
                dialog.dismiss();

                fragmentManager = getSupportFragmentManager();
                mapaFragment = new MapaFragment();
                fragmentManager.beginTransaction().replace(R.id.id_fragment_contenedor_mapa, mapaFragment).commit();
            }
        });

        listaprovincias.show();
    }

    public void SeleccionarCombustible()
    {
        String dbPath = new File(Environment.getExternalStorageDirectory().getPath() + DB_FILE).getAbsolutePath();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, dbPath);
        Database database = helper.getWritableDb();
        final DaoSession daoSession = new DaoMaster(database).newSession();

        combustibles = daoSession.getCombustibleDao().loadAll();

        AlertDialog.Builder listacombustibles = new AlertDialog.Builder(this);
        listacombustibles.setTitle("Seleccione el Combustible");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item);

        for (int i = 0; i < combustibles.size(); i++)
        {
            arrayAdapter.add(combustibles.get(i).getNombre());
        }

        listacombustibles.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Toast toast = Toast.makeText(getApplicationContext(), "Combustible Seleccionado: " + combustibles.get(which).getNombre(), Toast.LENGTH_SHORT);
                toast.show();
                dialog.dismiss();
            }
        });

        listacombustibles.show();
    }
}
