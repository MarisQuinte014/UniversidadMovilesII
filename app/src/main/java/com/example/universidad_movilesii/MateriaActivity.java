package com.example.universidad_movilesii;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MateriaActivity extends AppCompatActivity {


    EditText jetcodigomateria, jetnombremateria, jetcreditos, jetnombreprofesor;
    Switch jcbactivomateria;
    String codigoMateria, nombreMateria, creditos, nombreProfesor, id_documento;
    Button jbtnguardar, jbtncancelar,jbtnactivar, jbtnregresar;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_materia);
        jetcodigomateria = findViewById(R.id.etcodigomateria);
        jetnombremateria = findViewById(R.id.etnombremateria);
        jetcreditos = findViewById(R.id.etcreditos);
        jetnombreprofesor = findViewById(R.id.etnombreprofesor);
        jcbactivomateria = findViewById(R.id.cbactivo);
        jbtnguardar = findViewById(R.id.guardarmateria);
        jbtncancelar = findViewById(R.id.cancelarmateria);
        jbtnactivar = findViewById(R.id.activarmateria);

        id_documento = "";

        jetcreditos.setEnabled(false);
        jetnombremateria.setEnabled(false);
        jetnombreprofesor.setEnabled(false);
        jbtnactivar.setEnabled(false);
        jbtncancelar.setEnabled(false);
        jbtnguardar.setEnabled(false);
    }


    public void AdicionarMateria(View view){
        codigoMateria = jetcodigomateria.getText().toString();
        nombreMateria = jetnombremateria.getText().toString();
        creditos = jetcreditos.getText().toString();
        nombreProfesor = jetnombreprofesor.getText().toString();

        if(codigoMateria.isEmpty() || nombreMateria.isEmpty() || creditos.isEmpty() || nombreProfesor.isEmpty()){
            Toast.makeText(this, "Todos los datos son requeridos", Toast.LENGTH_SHORT).show();
            jetcodigomateria.requestFocus();
        }
        else {
            Map<String, Object> materia = new HashMap<>();
            materia.put("CodigoMateria", codigoMateria);
            materia.put("NombreMateria", nombreMateria);
            materia.put("Creditos", creditos);
            materia.put("NombreProfesor", nombreProfesor);
            materia.put("Activo", "Si");

                Consultar_Documento();
                if (!id_documento.equals("")) {
                    jetcodigomateria.setEnabled(true);
                    db.collection("Materia")
                            .add(materia)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                   // Limpiar_datos();
                                    Toast.makeText(MateriaActivity.this, "Datos guardados", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MateriaActivity.this, "Error guardando datos", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else {
                    jetcreditos.setEnabled(true);
                }
            }
    }

    private void Consultar_Documento(){
        codigoMateria = jetcodigomateria.getText().toString();
        if(codigoMateria.isEmpty()){
            Toast.makeText(this, "Debes digitar el código de la materia", Toast.LENGTH_SHORT).show();
            jetcodigomateria.requestFocus();
        }else {
            db.collection("Materia")
                    .whereEqualTo("CodigoMateria",codigoMateria)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    // Log.d(TAG, document.getId() + " => " + document.getData());
                                    id_documento = document.getId();
                                    jetnombremateria.setText(document.getString("NombreMateria"));
                                    jetcreditos.setText(document.getString("Creditos"));
                                    jetnombreprofesor.setText(document.getString("NombreProfesor"));
                                    if(document.getString("Activo").equals("Si"))
                                        jcbactivomateria.setChecked(true);
                                    else
                                        jcbactivomateria.setChecked(false);
                                }
                            } else {
                                //Log.w(TAG, "Error getting documents.", task.getException());
                                Toast.makeText(MateriaActivity.this, "Documento no hallado", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public void ConsularMateria(View view){
        Consultar_Documento();
    }
}