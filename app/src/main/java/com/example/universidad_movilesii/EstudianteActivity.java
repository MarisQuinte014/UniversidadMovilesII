package com.example.universidad_movilesii;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class EstudianteActivity extends AppCompatActivity {

    EditText jetcarnet, jetnombre, jetcarrera, jetsemestre;
    Switch jcbactivo;
    String carnet, nombre, carrera, semestre, id_documento;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estudiante);
        getSupportActionBar().hide();
        jetcarnet = findViewById(R.id.etcarnet);
        jetnombre = findViewById(R.id.etnombre);
        jetcarrera = findViewById(R.id.etcarrera);
        jetsemestre = findViewById(R.id.etsemestre);
        jcbactivo = findViewById(R.id.cbactivo);
        id_documento = "";
    }
    public void Adicionar(View view){
        carnet=jetcarnet.getText().toString();
        nombre=jetnombre.getText().toString();
        carrera=jetcarrera.getText().toString();
        semestre=jetsemestre.getText().toString();
        //Validar que todos los datos fueron ingresados
        if (carnet.isEmpty() || nombre.isEmpty() || carrera.isEmpty() || semestre.isEmpty()){
            Toast.makeText(this, "Todos los datos son requeridos", Toast.LENGTH_SHORT).show();
            jetcarnet.requestFocus();
        }else {
            // Create a new student with a first and last name
            Map<String, Object> estudiante = new HashMap<>();
            estudiante.put("Carnet", carnet);
            estudiante.put("Nombre", nombre);
            estudiante.put("Carrera", carrera);
            estudiante.put("Semestre", semestre);
            estudiante.put("Activo", "Si");

            // Add a new document with a generated ID
            ConsultarDocumento();
            if (!id_documento.equals("")) {
                db.collection("Estudiantes")
                        .add(estudiante)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                // Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                Limpiar_datos();
                                Toast.makeText(EstudianteActivity.this, "Datos guardados", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Log.w(TAG, "Error adding document", e);
                                Toast.makeText(EstudianteActivity.this, "Error guardando datos", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            else {
                Toast.makeText(this, "Documento ya existe", Toast.LENGTH_SHORT).show();
                Limpiar_datos();
            }
        }
    } //fin adicionar
    private void ConsultarDocumento(){
        carnet = jetcarnet.getText().toString();
        if(carnet.isEmpty()){
            Toast.makeText(this, "Debes digitar el numero de carnet", Toast.LENGTH_SHORT).show();
            jetcarnet.requestFocus();
        }else {
            db.collection("Estudiantes")
                    .whereEqualTo("Carnet",carnet)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                   // Log.d(TAG, document.getId() + " => " + document.getData());
                                    id_documento = document.getId();
                                    jetnombre.setText(document.getString("Nombre"));
                                    jetcarrera.setText(document.getString("Carrera"));
                                    jetsemestre.setText(document.getString("Semestre"));
                                    if(document.getString("activo").equals("Si"))
                                        jcbactivo.setChecked(true);
                                    else
                                        jcbactivo.setChecked(false);
                                }
                            } else {
                                //Log.w(TAG, "Error getting documents.", task.getException());
                                Toast.makeText(EstudianteActivity.this, "Docuemnto no hallado", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    public void Modificar(View view){
        if(!id_documento.equals("")){
            carnet = jetcarnet.getText().toString();
            nombre = jetnombre.getText().toString();
            carrera = jetcarrera.getText().toString();
            semestre = jetsemestre.getText().toString();

            Map<String, Object> estudiante = new HashMap<>();
            estudiante.put("Carnet", carnet);
            estudiante.put("Carrera", carrera);
            estudiante.put("Nombre", nombre);
            estudiante.put("Semestre", semestre);
            if(jcbactivo.isChecked())
                estudiante.put("activo", "Si");
            else
                estudiante.put("activo", "No");

            db.collection("Estudiantes").document(id_documento)
                    .set(estudiante)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(EstudianteActivity.this, "Documento actualizado", Toast.LENGTH_SHORT).show();
                            Limpiar_datos();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EstudianteActivity.this, "Error actualizando", Toast.LENGTH_SHORT).show();
                        }
                    });
        }else{
            Toast.makeText(this, "Debe consultar para poder modificar", Toast.LENGTH_SHORT).show();
            jetcarnet.requestFocus();
        }
    }
    public void Consultar(View view){
        ConsultarDocumento();
    }
    public void Eliminar(View view){
        if(!id_documento.equals("")){
            db.collection("Estudiantes").document(id_documento)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(EstudianteActivity.this, "Eliminado", Toast.LENGTH_SHORT).show();
                            Limpiar_datos();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EstudianteActivity.this, "Error eliminando", Toast.LENGTH_SHORT).show();
                        }
                    });
        }else {
            Toast.makeText(this, "Debe primero consultar para eliminar", Toast.LENGTH_SHORT).show();
            jetcarnet.requestFocus();
        }
    }
    public void Cancelar(View view){
        Limpiar_datos();
    }
    public void Anlular(View view){
        if(!id_documento.equals("")){
            carnet = jetcarnet.getText().toString();
            nombre = jetnombre.getText().toString();
            carrera = jetcarrera.getText().toString();
            semestre = jetsemestre.getText().toString();

            Map<String, Object> estudiante = new HashMap<>();
            estudiante.put("Carnet", carnet);
            estudiante.put("Carrera", carrera);
            estudiante.put("Nombre", nombre);
            estudiante.put("Semestre", semestre);
            estudiante.put("activo", "No");

            db.collection("Estudiantes").document(id_documento)
                    .set(estudiante)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(EstudianteActivity.this, "Documento anulado", Toast.LENGTH_SHORT).show();
                            Limpiar_datos();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EstudianteActivity.this, "Error anulando", Toast.LENGTH_SHORT).show();
                        }
                    });
        }else{
            Toast.makeText(this, "Debe consultar para poder modificar", Toast.LENGTH_SHORT).show();
            jetcarnet.requestFocus();
        }
    }
    public void Regresar(View view){
        Intent intMain = new Intent(this, MainActivity.class);
        startActivity(intMain);
    }
    private void Limpiar_datos(){
        jetcarnet.setText("");
        jetsemestre.setText("");
        jetcarrera.setText("");
        jetnombre.setText("");
        jcbactivo.setChecked(false);
        jetcarnet.requestFocus();
        id_documento = "";
    }
}