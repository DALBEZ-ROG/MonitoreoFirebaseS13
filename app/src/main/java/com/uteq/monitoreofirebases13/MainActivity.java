package com.uteq.monitoreofirebases13;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    // ===== Diapositiva 13: declaración de la base de datos y referencias =====
    private FirebaseDatabase database;
    private DatabaseReference temperaturaRef;
    private DatabaseReference humedadRef;
    private DatabaseReference presionRef;
    private DatabaseReference velocidadRef;

    // Elementos de UI
    private TextView valorTemperatura;
    private TextView valorHumedad;
    private TextView valorPresion;
    private TextView valorVelocidad;
    private EditText setvalorTemperatura;
    private EditText setvalorHumedad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Fuerza el modo oscuro en toda la app, sin importar el ajuste del sistema
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ===== Diapositiva 13: inicialización de Firebase =====
        // Si getInstance() fallara por un google-services.json sin firebase_url,
        // usar: FirebaseDatabase.getInstance("https://fir-monitoreos13-default-rtdb.firebaseio.com/")
        database = FirebaseDatabase.getInstance();

        // findViewById de los elementos de UI (monitoreo y seteo)
        valorTemperatura = findViewById(R.id.valor_Temperatura);
        valorHumedad = findViewById(R.id.valor_Humedad);
        valorPresion = findViewById(R.id.valor_Presion);
        valorVelocidad = findViewById(R.id.valor_Velocidad);
        setvalorTemperatura = findViewById(R.id.setvalor_Temperatura);
        setvalorHumedad = findViewById(R.id.setvalor_Humedad);

        // Referencias a los nodos de la ruta sensores/
        temperaturaRef = database.getReference("sensores/temperatura");
        humedadRef = database.getReference("sensores/humedad");
        presionRef = database.getReference("sensores/presion");
        velocidadRef = database.getReference("sensores/velocidad");

        // ===== Diapositiva 14: se adjunta un listener a cada referencia =====
        temperaturaRef.addValueEventListener(setListener(valorTemperatura, "°C"));
        humedadRef.addValueEventListener(setListener(valorHumedad, "%"));
        presionRef.addValueEventListener(setListener(valorPresion, "hPa"));
        velocidadRef.addValueEventListener(setListener(valorVelocidad, "km/h"));
    }

    // ===== Diapositiva 14: listener genérico de lectura en tiempo real =====
    public ValueEventListener setListener(TextView txt, String unidadMedida) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Object valor = snapshot.getValue();
                if (valor != null) {
                    txt.setText(valor.toString() + " " + unidadMedida);
                } else {
                    txt.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                txt.setText("");
            }
        };
    }

    // ===== Diapositiva 15: escritura en la base de datos (botones Set) =====
    public void clickBotonTemp(View view) {
        try {
            float valor = Float.parseFloat(setvalorTemperatura.getText().toString());
            temperaturaRef.setValue(valor);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ingresa un valor numérico válido", Toast.LENGTH_SHORT).show();
        }
    }

    public void clickBotonHumedad(View view) {
        try {
            float valor = Float.parseFloat(setvalorHumedad.getText().toString());
            humedadRef.setValue(valor);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ingresa un valor numérico válido", Toast.LENGTH_SHORT).show();
        }
    }
}
