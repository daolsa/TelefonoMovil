package com.example.telefonomovil;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int NUMEROS = 10;
    private static final int MAX_NUM = 15;
    private TextView textoTelefono;

    /**
     * ActivityResultLauncher<String> es el lanzador de los permisos requeridos en nuestra aplicación.
     * Entrada: ActivityResultCallback<Boolean>: Define el modo en el que tu app controla la respuesta del usuario a la solicitud del permiso.
     * RequestPermission -> instancia para solicitar un solo permiso. Para varios, utilizar RequestMultiplePermissions.
     */

    private ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                // Acciones a realizar en el caso de que el usuario acepte POR PRIMERA VEZ los permisos
                @Override
                public void onActivityResult(Boolean isGranted) {
                    if (isGranted) {
                        lanzarLlamadaTelefono();
                    } else {
                        Toast.makeText(MainActivity.this, "No hay permisos necesarios", Toast.LENGTH_LONG).show();

                    }
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iniciarBotones();
        textoTelefono = findViewById(R.id.textoTelefono);

    }

    // Método para poner el listener a todos los botones que conforman el layout
    public void iniciarBotones() {

        int id = 0;
        Button button;
        ImageButton imageButton;

        for (int i = 0; i < NUMEROS; i++) {
            id = getResources().getIdentifier("boton" + i, "id", this.getPackageName());
            button = findViewById(id);
            button.setOnClickListener(this);
        }

        button = findViewById(R.id.botonArrow);
        button.setOnClickListener(this);
        button = findViewById(R.id.botonc);
        button.setOnClickListener(this);
        imageButton = findViewById(R.id.botonTelefono);
        imageButton.setOnClickListener(this);


    }

    // Método para lanzar el intent necesario para la llamada de teléfono

    public void lanzarLlamadaTelefono() {
        // Constructores interesantes: (String action) ; (String action, Uri uri) ; (Context packageContext, Class class)

        String telefono = textoTelefono.getText().toString();
        if (!(telefono.equals(getResources().getString(R.string.mensaje_inicial)))) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            Uri uri = Uri.parse("tel:" + telefono);
            intent.setData(uri);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, getResources().getString(R.string.error_resolver_actividad), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.error_no_telefono), Toast.LENGTH_LONG).show();
        }
    }

    // Método para comprobar si contamos con el permiso necesario
    public boolean tienePermisosLlamada() {

        boolean permisos = false;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
            permisos = true;

        return permisos;

    }


    // En la proxima actualización los id de los botones no serán constantes, por lo que la estructura
    // switch no se podrá utilizar. En su lugar, tendremos que cambiarla por if-else anidados

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        String texto = "";
        String numero = "";
        switch (v.getId()) {
            case R.id.botonc:
                textoTelefono.setText(getResources().getString(R.string.mensaje_inicial));
                break;
            case R.id.botonArrow:
                texto = textoTelefono.getText().toString();
                if (!texto.equals(getResources().getString(R.string.mensaje_inicial))) {
                    if (texto.length() > 1) {
                        //int index = texto.lastIndexOf(texto.charAt(texto.length()-1));
                        texto = texto.substring(0, texto.length() - 1);
                        textoTelefono.setText(texto);
                    } else {
                        textoTelefono.setText(getResources().getString(R.string.mensaje_inicial));
                    }
                }
                break;
            case R.id.botonTelefono:
                // Solicitar permiso en tiempo de ejecución... Si lo tiene, palante...
                if (tienePermisosLlamada()) {
                    lanzarLlamadaTelefono();
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.CALL_PHONE);
                }
                break;
            default:

                Toast.makeText(this,((Button) v).getText().toString(),Toast.LENGTH_LONG).show();
                texto = textoTelefono.getText().toString();

                numero = getResources().getResourceEntryName(v.getId());
                numero = numero.replace("boton", "");

                if (texto.equals(getResources().getString(R.string.mensaje_inicial))) {
                    texto = numero;
                } else {
                    if (texto.length() < MAX_NUM) {
                        texto = textoTelefono.getText().toString() + numero;
                    }
                }
                textoTelefono.setText(texto);
        }
    }
}