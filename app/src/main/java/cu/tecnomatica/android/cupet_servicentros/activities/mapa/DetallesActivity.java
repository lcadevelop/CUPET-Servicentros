package cu.tecnomatica.android.cupet_servicentros.activities.mapa;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import cu.tecnomatica.android.cupet_servicentros.R;

public class DetallesActivity extends AppCompatActivity
{
    TextView textViewNombre;
    TextView textViewDireccion;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });

        final Bundle bundle = this.getIntent().getExtras();

        textViewNombre = (TextView)findViewById(R.id.id_texto_nombre_llenar);
        textViewNombre.setText(bundle.getString("Nombre"));

        textViewDireccion = (TextView)findViewById(R.id.id_texto_direccion_llenar);
        textViewDireccion.setText(bundle.getString("Direccion"));
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return false;
    }
}
