package com.example.universidad_movilesii;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
        getSupportActionBar().hide();
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
        jbtnguardar.setEnabled(false);
        jcbactivomateria.setEnabled(false);
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
            if(jcbactivomateria.isChecked())
                materia.put("Activo", "Si");
            else
                materia.put("Activo", "No");
            if(!id_documento.equals("")){
                db.collection("Materia").document(id_documento)
                        .set(materia)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(MateriaActivity.this, "Materia "+ codigoMateria + " actualizada", Toast.LENGTH_SHORT).show();
                                Cancelar();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MateriaActivity.this, "Error actualizando", Toast.LENGTH_SHORT).show();
                            }
                        });
            }else{
                db.collection("Materia")
                        .add(materia)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(MateriaActivity.this, "Materia "+ codigoMateria +" guardada", Toast.LENGTH_SHORT).show();
                                Cancelar();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MateriaActivity.this, "Error guardando datos", Toast.LENGTH_SHORT).show();
                            }
                        });
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
                                    id_documento = document.getId();
                                    jetnombremateria.setText(document.getString("NombreMateria"));
                                    jetcreditos.setText(document.getString("Creditos"));
                                    jetnombreprofesor.setText(document.getString("NombreProfesor"));
                                    jetcreditos.setEnabled(true);
                                    jetnombremateria.setEnabled(true);
                                    jetnombreprofesor.setEnabled(true);
                                    jetcodigomateria.setEnabled(false);
                                    jbtnguardar.setEnabled(true);
                                    jbtnactivar.setEnabled(true);
                                    jcbactivomateria.setEnabled(true);
                                    if(document.getString("Activo").equals("Si"))
                                        jcbactivomateria.setChecked(true);
                                    else
                                        jcbactivomateria.setChecked(false);
                                }
                                if(id_documento.equals("")){
                                    Toast.makeText(MateriaActivity.this, "Estas habilitado para guardar este código de materia", Toast.LENGTH_SHORT).show();
                                    jetcreditos.setEnabled(true);
                                    jetnombremateria.setEnabled(true);
                                    jetnombreprofesor.setEnabled(true);
                                    jbtnguardar.setEnabled(true);
                                }
                            } else {
                                Toast.makeText(MateriaActivity.this, "La tarea no pudo ser completada exitosamente", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    public void ConsularMateria(View view){
        Consultar_Documento();
    }
    public void ActivarMateria(View view){
        if(!id_documento.equals("")){
            if(jcbactivomateria.isChecked()){
                Toast.makeText(this, "La materia ya se encuentra activa", Toast.LENGTH_SHORT).show();
            }else {
                codigoMateria = jetcodigomateria.getText().toString();
                nombreMateria = jetnombremateria.getText().toString();
                creditos = jetcreditos.getText().toString();
                nombreProfesor = jetnombreprofesor.getText().toString();
                Map<String, Object> materia = new HashMap<>();
                materia.put("CodigoMateria", codigoMateria);
                materia.put("NombreMateria", nombreMateria);
                materia.put("Creditos", creditos);
                materia.put("NombreProfesor", nombreProfesor);
                materia.put("Activo", "Si");
                db.collection("Materia").document(id_documento)
                        .set(materia)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(MateriaActivity.this, "Documento activado", Toast.LENGTH_SHORT).show();
                                Cancelar();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MateriaActivity.this, "Error activado", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }else{
            Toast.makeText(this, "Primero debes agregar para poder activar", Toast.LENGTH_SHORT).show();
        }
    }
    public void CancelarMateria(View view){
        Cancelar();
    }
    private void Cancelar(){
        jetcodigomateria.setText("");
        jetnombremateria.setText("");
        jetcreditos.setText("");
        jetnombreprofesor.setText("");
        jetcodigomateria.setEnabled(true);
        jetnombremateria.setEnabled(false);
        jetcreditos.setEnabled(false);
        jetnombreprofesor.setEnabled(false);
        jcbactivomateria.setChecked(true);
        jcbactivomateria.setEnabled(false);
        jbtnguardar.setEnabled(false);
        jbtnactivar.setEnabled(false);

    }
    public void RegresarInicio(View view){
        Intent intMain = new Intent(this, MainActivity.class);
        startActivity(intMain);
    }
}