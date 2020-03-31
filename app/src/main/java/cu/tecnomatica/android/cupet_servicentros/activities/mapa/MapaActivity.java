package cu.tecnomatica.android.cupet_servicentros.activities.mapa;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.greenrobot.greendao.database.Database;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.util.List;
import cu.tecnomatica.android.cupet_servicentros.R;
import cu.tecnomatica.android.cupet_servicentros.activities.ayuda.AyudaActivity;
import cu.tecnomatica.android.cupet_servicentros.activities.ayuda.CreditosActivity;
import cu.tecnomatica.android.cupet_servicentros.database.Auxiliar;
import cu.tecnomatica.android.cupet_servicentros.database.Combustible;
import cu.tecnomatica.android.cupet_servicentros.database.DaoMaster;
import cu.tecnomatica.android.cupet_servicentros.database.DaoSession;
import cu.tecnomatica.android.cupet_servicentros.database.Provincia;
import cu.tecnomatica.android.cupet_servicentros.database.Servicentro;
import cu.tecnomatica.android.cupet_servicentros.database.ServicentroDao;

public class MapaActivity extends AppCompatActivity
{
    FragmentManager fragmentManager = getSupportFragmentManager();
    MapaFragment mapaFragment = new MapaFragment();

    private static final String DB_FILE = "/CUPET/servi.db";
    private static final String api_url = "http://siocunion.cupet.cu/api/ServicentrosApk/";

    RequestQueue queue;

    List<Provincia> provincias;
    List<Combustible> combustibles;

    private ProgressBar progressBar;
    private ObjectAnimator objectAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        queue = Volley.newRequestQueue(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        fragmentManager.beginTransaction().replace(R.id.id_fragment_contenedor_mapa, mapaFragment).commit();

        BottomNavigationView top_menu_navigation = (BottomNavigationView)findViewById(R.id.id_top_navigation);
        top_menu_navigation.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
            {
            }
        });

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

                    case R.id.id_navigation_menu_servicentros:
                        SeleccionarServicentros();
                        return true;
                }
                return false;
            }
        });

        String dbPath = new File(Environment.getExternalStorageDirectory().getPath() + DB_FILE).getAbsolutePath();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, dbPath);
        Database database = helper.getWritableDb();
        final DaoSession daoSession = new DaoMaster(database).newSession();

        List<Auxiliar> auxiliars = daoSession.getAuxiliarDao().loadAll();

        if (auxiliars.get(0).getCarga_inicial())
        {
            auxiliars.get(0).setCarga_inicial(false);
            daoSession.insertOrReplace(auxiliars.get(0));
            SeleccionarProvincia();
        }
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

                Provincia activa = SeleccionarProvinciaActiva();
                mapaFragment.RecentrarMapa(Double.parseDouble(activa.getLatitud()), Double.parseDouble(activa.getLongitud()), false);

                ActualizarServicentros();
            }
        });

        listaprovincias.show();
    }

    public Provincia SeleccionarProvinciaActiva()
    {
        Provincia provinciaactiva = new Provincia();

        String dbPath = new File(Environment.getExternalStorageDirectory().getPath() + DB_FILE).getAbsolutePath();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, dbPath);
        Database database = helper.getWritableDb();
        final DaoSession daoSession = new DaoMaster(database).newSession();

        provincias = daoSession.getProvinciaDao().loadAll();

        for (int i = 0; i < provincias.size(); i++)
        {
            if (provincias.get(i).getActiva())
            {
                provinciaactiva = provincias.get(i);
            }
        }
        return provinciaactiva;
    }

    public Combustible SeleccionarCombustibleActivo()
    {
        Combustible combustibleavtivo = new Combustible();

        String dbPath = new File(Environment.getExternalStorageDirectory().getPath() + DB_FILE).getAbsolutePath();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, dbPath);
        Database database = helper.getWritableDb();
        final DaoSession daoSession = new DaoMaster(database).newSession();

        combustibles = daoSession.getCombustibleDao().loadAll();

        for (int i = 0; i < combustibles.size(); i++)
        {
            if (combustibles.get(i).getActivo())
            {
                combustibleavtivo = combustibles.get(i);
            }
        }
        return combustibleavtivo;
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

        for (int i = combustibles.size() -1; i >= 0; i--)
        {
            arrayAdapter.add(combustibles.get(i).getNombre());
        }

        listacombustibles.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String combustibleseleccionado = arrayAdapter.getItem(which);

                for (int i = 0; i < combustibles.size(); i++)
                {
                    Combustible combustibleTemporal = combustibles.get(i);
                    if (combustibleTemporal.getNombre().equals(combustibleseleccionado))
                    {
                        combustibleTemporal.setActivo(true);
                        daoSession.insertOrReplace(combustibleTemporal);
                    }
                    else
                    {
                        combustibleTemporal.setActivo(false);
                        daoSession.insertOrReplace(combustibleTemporal);
                    }
                }

                Toast toast = Toast.makeText(getApplicationContext(), "Combustible Seleccionado: " + combustibles.get(which).getNombre(), Toast.LENGTH_SHORT);
                toast.show();
                dialog.dismiss();

                ActualizarServicentros();
            }
        });

        listacombustibles.show();
    }

    public void SeleccionarServicentros()
    {
        String dbPath = new File(Environment.getExternalStorageDirectory().getPath() + DB_FILE).getAbsolutePath();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, dbPath);
        Database database = helper.getWritableDb();
        final DaoSession daoSession = new DaoMaster(database).newSession();

        Provincia provinciaactiva = SeleccionarProvinciaActiva();
        final List<Servicentro> servicentrosxprovincia = daoSession.getServicentroDao().queryBuilder().where(ServicentroDao.Properties.Idprovincia.like(provinciaactiva.getIdprovincia().toString())).list();

        AlertDialog.Builder listaservicentros = new AlertDialog.Builder(this);
        listaservicentros.setTitle("Servicentros de " + provinciaactiva.getNombre());

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item);

        for (int i = 0; i < servicentrosxprovincia.size(); i++)
        {
            arrayAdapter.add(servicentrosxprovincia.get(i).getNombre());
        }

        listaservicentros.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                double latitud = Double.parseDouble(servicentrosxprovincia.get(which).getLatitud());
                double longitud = Double.parseDouble(servicentrosxprovincia.get(which).getLongitud());
                mapaFragment.RecentrarMapa(latitud, longitud, true);

                Toast toast = Toast.makeText(getApplicationContext(), "Servicentro Seleccionado: " + servicentrosxprovincia.get(which).getNombre(), Toast.LENGTH_SHORT);
                toast.show();
                dialog.dismiss();
            }
        });

        listaservicentros.show();
    }

    public void ActualizarServicentros()
    {
        //MostrarProgreso();
        Provincia provinciaactiva = SeleccionarProvinciaActiva();

        String urlAPI = api_url + provinciaactiva.getIdprovincia();

        String dbPath = new File(Environment.getExternalStorageDirectory().getPath() + DB_FILE).getAbsolutePath();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, dbPath);
        Database database = helper.getWritableDb();
        final DaoSession daoSession = new DaoMaster(database).newSession();

        final List<Servicentro> servicentrosxprovincia = daoSession.getServicentroDao().queryBuilder().where(ServicentroDao.Properties.Idprovincia.like(provinciaactiva.getIdprovincia().toString())).list();

        try
        {
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(urlAPI, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response)
                {
                    try
                    {
                        for (int i = 0; i < response.length(); i++)
                        {
                            JSONObject jsonObject = response.getJSONObject(i);

                            String id = jsonObject.getString("id");

                            for (int k = 0; k < servicentrosxprovincia.size(); k++)
                            {
                                if (servicentrosxprovincia.get(k).getCodigo() == id)
                                {
                                    Servicentro servicentro = servicentrosxprovincia.get(k);

                                    JSONArray jsonArray = jsonObject.getJSONArray("productos");

                                    for (int j = 0; j < jsonArray.length(); j++)
                                    {
                                        JSONObject jsonObject2 = jsonArray.getJSONObject(j);
                                        String idproducto = jsonObject2.getString("idProducto");
                                        String fechaservido = jsonObject2.getString("fechaServido");
                                        String fechamostrar = fechaservido.substring(0, 10);

                                        switch (idproducto)
                                        {
                                            case "1":
                                                servicentro.setDiesel(true);
                                                servicentro.setFechadiesel(fechamostrar);
                                                break;

                                            case "2":
                                                servicentro.setMotor(true);
                                                servicentro.setFechamotor(fechamostrar);
                                                break;

                                            case "3":
                                                servicentro.setRegular(true);
                                                servicentro.setFecharegular(fechamostrar);
                                                break;

                                            case "4":
                                                servicentro.setEspecial(true);
                                                servicentro.setFechaespecial(fechamostrar);
                                                break;

                                            case "5":
                                                servicentro.setPremium(true);
                                                servicentro.setFechapremium(fechamostrar);
                                                break;
                                        }
                                    }

                                    daoSession.insertOrReplace(servicentro);
                                }
                            }
                        }
                    }
                    catch (JSONException e)
                    {
                        Log.e("Errorrr ", e.getMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    Log.e("Error Message: ", error.getMessage());
                }
            });
            queue.add(jsonArrayRequest);
        }
        catch (Exception e)
        {
            Log.e("ErrorRR: ", e.getMessage());
        }
    }

    public void MostrarProgreso()
    {
        progressBar = (ProgressBar)findViewById(R.id.idprogressbar);
        progressBar.setVisibility(View.VISIBLE);
        objectAnimator = ObjectAnimator.ofInt(progressBar, "progress",0, 100);
        objectAnimator.setDuration(15);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.start();
    }
}
