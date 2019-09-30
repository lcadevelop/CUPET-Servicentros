package cu.tecnomatica.android.cupet_servicentros.activities.mapa;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.greenrobot.greendao.database.Database;
import org.oscim.android.MapView;
import org.oscim.android.canvas.AndroidGraphics;
import org.oscim.backend.CanvasAdapter;
import org.oscim.core.GeoPoint;
import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.renderer.GLViewport;
import org.oscim.scalebar.DefaultMapScaleBar;
import org.oscim.scalebar.MapScaleBar;
import org.oscim.scalebar.MapScaleBarLayer;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.source.mapfile.MapFileTileSource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import cu.tecnomatica.android.cupet_servicentros.R;
import cu.tecnomatica.android.cupet_servicentros.database.DaoMaster;
import cu.tecnomatica.android.cupet_servicentros.database.DaoSession;
import cu.tecnomatica.android.cupet_servicentros.database.Provincia;
import cu.tecnomatica.android.cupet_servicentros.database.Servicentro;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapaFragment extends Fragment
{
    private OnFragmentInteractionListener mListener;

    private static final String MAP_FILE = "/CUPET/cuba.map";
    private static final String DB_FILE = "/CUPET/servi.db";
    MapView mapView;
    MapScaleBar mapScaleBar;

    ArrayList<MarkerItem> arrayListFull;
    ArrayList<MarkerItem> arrayListEmpty;

    List<Provincia> provincias;
    List<Servicentro> servicentros;

    public MapaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapaFragment newInstance(String param1, String param2) {
        MapaFragment fragment = new MapaFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_mapa, container, false);

        String dbPath = new File(Environment.getExternalStorageDirectory().getPath() + DB_FILE).getAbsolutePath();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getActivity().getApplicationContext(), dbPath);
        Database database = helper.getWritableDb();
        DaoSession daoSession = new DaoMaster(database).newSession();

        provincias = daoSession.getProvinciaDao().loadAll();
        servicentros = daoSession.getServicentroDao().loadAll();

        Provincia provinciaactiva = ObtenerPronvinciaAvitva();

        mapView = view.findViewById(R.id.idMapa);
        arrayListFull = new ArrayList<>();
        arrayListEmpty = new ArrayList<>();

        MapFileTileSource tileSource = new MapFileTileSource();
        String mapPath = new File(Environment.getExternalStorageDirectory().getPath() + MAP_FILE).getAbsolutePath();
        if (tileSource.setMapFile(mapPath))
        {
            VectorTileLayer tileLayer = mapView.map().setBaseMap(tileSource);

            mapView.map().layers().add(new BuildingLayer(mapView.map(), tileLayer));

            LabelLayer labelLayer = new LabelLayer(mapView.map(), tileLayer);

            mapView.map().layers().add(labelLayer);

            mapView.map().setTheme(VtmThemes.DEFAULT);

            mapScaleBar = new DefaultMapScaleBar(mapView.map());
            MapScaleBarLayer mapScaleBarLayer = new MapScaleBarLayer(mapView.map(), mapScaleBar);
            mapScaleBarLayer.getRenderer().setPosition(GLViewport.Position.BOTTOM_LEFT);
            mapScaleBarLayer.getRenderer().setOffset(5 * CanvasAdapter.getScale(), 0);
            mapView.map().layers().add(mapScaleBarLayer);

            double lat = Double.parseDouble(provinciaactiva.getLatitud());
            double lon = Double.parseDouble(provinciaactiva.getLongitud());
            mapView.map().setMapPosition(lat, lon, 3 << 11);

            MostrarServicentros();
        }

        return view;
    }

    public Provincia ObtenerPronvinciaAvitva()
    {
        Provincia provinciaactiva = provincias.get(0);

        for (int i =0; i < provincias.size(); i++)
        {
            if(provincias.get(i).getActiva())
            {
                provinciaactiva = provincias.get(i);
            }
        }
        return provinciaactiva;
    }

    public void MostrarServicentros()
    {
        org.oscim.backend.canvas.Bitmap bitmapServiFull = AndroidGraphics.drawableToBitmap(getResources().getDrawable(R.drawable.ic_gas_station_full_24dp));
        MarkerSymbol symbolFull = new MarkerSymbol(bitmapServiFull, MarkerSymbol.HotspotPlace.BOTTOM_CENTER);
        org.oscim.backend.canvas.Bitmap bitmapServiEmpty = AndroidGraphics.drawableToBitmap(getResources().getDrawable(R.drawable.ic_gas_station_empty_24dp));
        MarkerSymbol symbolEmpty = new MarkerSymbol(bitmapServiEmpty, MarkerSymbol.HotspotPlace.BOTTOM_CENTER);

        arrayListFull = new ArrayList<>();
        arrayListEmpty = new ArrayList<>();
        double latitud = 0;
        double longitud = 0;
        GeoPoint geoPoint;
        MarkerItem markerItem;

        for (int i = 0; i < servicentros.size(); i++)
        {
            latitud = Double.parseDouble(servicentros.get(i).getLatitud());
            longitud = Double.parseDouble(servicentros.get(i).getLongitud());
            geoPoint = new GeoPoint(latitud, longitud);
            markerItem = new MarkerItem(servicentros.get(i).getCodigo(), servicentros.get(i).getNombre(), geoPoint);

            if (i % 2 == 0)
            {
                arrayListFull.add(markerItem);
            }
            else
            {
                arrayListEmpty.add(markerItem);
            }
        }

        ItemizedLayer<MarkerItem> markerItemItemizedLayerFull = new ItemizedLayer<MarkerItem>(mapView.map(), arrayListFull, symbolFull, new ItemizedLayer.OnItemGestureListener<MarkerItem>() {
            @Override
            public boolean onItemSingleTapUp(int index, MarkerItem item)
            {
                Intent intent = new Intent(getActivity(), DetallesActivity.class);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onItemLongPress(int index, MarkerItem item) {
                return false;
            }
        });
        ItemizedLayer<MarkerItem> markerItemItemizedLayerEmpty = new ItemizedLayer<MarkerItem>(mapView.map(), arrayListEmpty, symbolEmpty, new ItemizedLayer.OnItemGestureListener<MarkerItem>() {
            @Override
            public boolean onItemSingleTapUp(int index, MarkerItem item) {
                Intent intent = new Intent(getActivity(), DetallesActivity.class);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onItemLongPress(int index, MarkerItem item) {
                return false;
            }
        });

        mapView.map().layers().add(markerItemItemizedLayerFull);
        mapView.map().layers().add(markerItemItemizedLayerEmpty);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
