package com.example.proyectosegundaev;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PathEffect;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Operaciones extends AppCompatActivity {
    //variables para la autenticacion,de los elementos de la ventana y para las validaciones
    private DatabaseReference dbRef,dbref2;
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = mAuth.getCurrentUser();
    private ConstraintLayout back;
    private Button boton1,boton2,boton3,boton4;
    private EditText nombre,direccion,email,telefono;
    private TextView labelnombre,labeldireccion,labelemail,labeltelefono;
    private ListView lista;
    private Switch change_theme;
    private ImageButton llamada;
    Pattern regextel = Pattern.compile("^[0-9]{9}$");
    Pattern regexnom = Pattern.compile("^[a-zA-Z\\s'-]+$");
    Pattern regexdir = Pattern.compile("[a-zA-Z\\s'-]+[0-9]{1}+[A-Z]{1}");
    Pattern regexemail=Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");


    @Override
    //funcion que detecta si existe el nodo y dependiendo de ello lo crea o no
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operaciones);
        Button save=findViewById(R.id.botonGuardar);
        //usuario actual
        String userEmail = firebaseUser.getUid();
        //referencias a la base de datos
        dbRef = FirebaseDatabase.getInstance().getReference("usuarios");
        dbref2= FirebaseDatabase.getInstance().getReference("usuarios").child(firebaseUser.getUid());
        //evento que nos comprueba si existe o no el nodo
        dbref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //si existe mensaje de que no se ha creado nada
                    Toast.makeText(Operaciones.this, "No se ha creado un nodo",
                            Toast.LENGTH_SHORT).show();

                } else {
                    //creacion del nodo
                    Map<String,String> userData = new HashMap<>();
                    userData.put("contactos","");
                    dbRef.child(userEmail).setValue(userData);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Ocurrió un error al intentar leer el valor.
            }
        });

        llamada=findViewById(R.id.llamada);
        //funcion que al clickar sobre la imagen detectara el contacto que esta puesto y le llamara
        llamada.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           //referencia al nodo de la base de datos
                                           dbRef = FirebaseDatabase.getInstance().getReference("usuarios").child(firebaseUser.getUid()).child("contactos");
                                           EditText telefono = findViewById(R.id.telefono);
                                           Query query = dbRef.orderByChild("telefono").equalTo(telefono.getText().toString());
                                           //evento para detectar que ese numero existe en la base de datos
                                           query.addListenerForSingleValueEvent(new ValueEventListener() {
                                               @Override
                                               public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                   for(DataSnapshot ds:snapshot.getChildren()){
                                                       if(snapshot.exists()) {
                                                           //si existe nos llevara a la llamada
                                                           Intent intent = new Intent(Intent.ACTION_DIAL);
                                                            // Establece el número de teléfono en el Intent
                                                           intent.setData(Uri.parse("tel:" + telefono.getText().toString()));
                                                            // Inicia el Intent
                                                           startActivity(intent);
                                                       }
                                                       else{
                                                           //mensaje de que el telefono no existe
                                                           Toast.makeText(Operaciones.this, "No existe ese telefono en sus contactos.",
                                                                   Toast.LENGTH_SHORT).show();
                                                       }

                                                   }
                                               }

                                               @Override
                                               public void onCancelled(@NonNull DatabaseError error) {

                                               }
                                           });
                                       }



                                       });
        //evento que al clickar sobre el boton guardar nos guardara el contenido en la base de datos
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //referencia al nodo de la base de datos
                dbRef = FirebaseDatabase.getInstance().getReference("usuarios").child(firebaseUser.getUid()).child("contactos");
                nombre = findViewById(R.id.nombre);
                direccion = findViewById(R.id.direccion);
                email = findViewById(R.id.correo);
                telefono = findViewById(R.id.telefono);
                //validacion del contenido
                Matcher matchernom=regexnom.matcher(nombre.getText().toString());
                Matcher matcheremail=regexemail.matcher(email.getText().toString());
                Matcher matcherdir=regexdir.matcher(direccion.getText().toString());
                Matcher matchertel=regextel.matcher(telefono.getText().toString().trim());
                if (matchernom.find()==true && matcheremail.find()==true && matcherdir.find()==true && matchertel.find()==true) {
                    //si son validos los campos creacion del contaco y se guarda enseñando un mensaje de exito
                    Contacto c = new Contacto(nombre.getText().toString(), direccion.getText().toString(), email.getText().toString(), telefono.getText().toString());

                    dbRef.push().setValue(c);
                    Toast.makeText(Operaciones.this, " Datos insertados correctamente.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    //mensaje de datos invalidos
                    Toast.makeText(Operaciones.this, " Datos invalidos",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button listar=findViewById(R.id.botonListar);
        //evento que al clickar sobre el boton de listado nos listara los contactos de la base de datos
        listar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lista= findViewById(R.id.listaNombres);
                Contacto c=new Contacto();
                //referencia al nodo de la base de datos
                dbRef= FirebaseDatabase.getInstance().getReference("usuarios").child(firebaseUser.getUid()).child("contactos");                dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //recogida de los datos de la base de datos a la lista
                        Contacto c;
                        ArrayAdapter<String> adapter;
                        ArrayList<String> list =new ArrayList<String>();
                        for (DataSnapshot ds:snapshot.getChildren()){
                            c= ds.getValue(Contacto.class);
                            list.add(c.toString());
                        }
                        //introduccion de los datos a la lista
                        adapter = new ArrayAdapter<>(Operaciones.this, android.R.layout.simple_list_item_1,list);
                        lista.setAdapter(adapter);
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        Button modificar=findViewById(R.id.botonModificar);
        //evento que al clickar sobre el boton modificar nos modificara los valores que hallamos escrito
        modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //referencia al nodo de la base de datos
                dbRef = FirebaseDatabase.getInstance().getReference("usuarios").child(firebaseUser.getUid()).child("contactos");
                EditText nombre = findViewById(R.id.nombre);
                EditText direccion = findViewById(R.id.direccion);
                EditText email = findViewById(R.id.correo);
                EditText telefono = findViewById(R.id.telefono);
                //validacion de los campos
                Matcher matchernom = regexnom.matcher(nombre.getText().toString());
                Matcher matcheremail = regexemail.matcher(email.getText().toString());
                Matcher matcherdir = regexdir.matcher(direccion.getText().toString());
                Matcher matchertel = regextel.matcher(telefono.getText().toString().trim());
                if (matchernom.find() == true && matcheremail.find() == true && matcherdir.find() == true && matchertel.find() == true) {
                    //si son valido se crea el contacto
                    Contacto c = new Contacto(nombre.getText().toString(), direccion.getText().toString(), email.getText().toString(), telefono.getText().toString().trim());

                    Query query = dbRef.orderByChild("nombre").equalTo(nombre.getText().toString());
                    //se comprueba mediante el nombre que ese contacto existe
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                //los datos se cambian
                                String clave = ds.getKey();

                                dbRef.child(clave).child("direccion").setValue(direccion.getText().toString());
                                dbRef.child(clave).child("email").setValue(email.getText().toString());
                                dbRef.child(clave).child("telefono").setValue(telefono.getText().toString());
                                Toast.makeText(Operaciones.this, "Datos modificados correctamente.",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else {
                    //en caso de que no exista el contacto
                    Toast.makeText(Operaciones.this, "Datos no modificados .",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button eliminar=findViewById(R.id.botonBorrar);
        //evento que al pulsar el boton de eliminar nos eliminara un contacto de la base de datos
        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //referencia al nodo de la base de datos
                dbRef= FirebaseDatabase.getInstance().getReference("usuarios").child(firebaseUser.getUid()).child("contactos");
                TextView nombre= findViewById(R.id.nombre);
                Query query= dbRef.orderByChild("nombre").equalTo(nombre.getText().toString());
                //comprueba que el contacto existe mediante el nombre
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds:snapshot.getChildren()){
                            if(snapshot.exists()) {
                                //si existe lo borra
                                String clave = ds.getKey();
                                dbRef.child(clave).removeValue();
                                Toast.makeText(Operaciones.this, "Borrado exitoso.",
                                        Toast.LENGTH_SHORT).show();
                            }
                            else{
                                //mensaje de error si no existe el contacto
                                Toast.makeText(Operaciones.this, "No existe ese nombre.",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        //elementos de la ventana para cambiar el estilo de estos
        back = (ConstraintLayout) findViewById(R.id.fondo);
        boton1= (Button) findViewById(R.id.botonBorrar);
        boton2= (Button) findViewById(R.id.botonGuardar);
        boton3= (Button) findViewById(R.id.botonModificar);
        boton4= (Button) findViewById(R.id.botonListar);
        change_theme = (Switch) findViewById(R.id.estilos);
        nombre=findViewById(R.id.nombre);
        direccion=findViewById(R.id.direccion);
        email=findViewById(R.id.correo);
        telefono=findViewById(R.id.telefono);
        lista=findViewById(R.id.listaNombres);
        labelnombre=findViewById(R.id.nombrelabel);
        labeldireccion=findViewById(R.id.direccionlabel);
        labelemail=findViewById(R.id.contraseñalabel);
        labeltelefono=findViewById(R.id.telefonolabel);

        //funcion que al activar o desactivar el switch cambiara el estilo de la app
        change_theme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //detecta si el switch esta marcado o no
                if(change_theme.isChecked()) {
                    //actualiza el tema al oscuro si esta marcado
                    updateTheme("DARK", "#212121", "#FFFFFF","#FFFFFF","#37474f");
                } else {
                    //si no estaa marcado deja el por defecto
                    updateTheme("DEFAULT", "#90EE90", "#8d4925","#FFFFFF","#FF3700B3");
                }
            }
        });

        loadTheme();
    }

    //funcion que nos permite actualizar el tema
    public void updateTheme(String key, String c1, String c2, String c3, String c4) {
        SharedPreferences savePreferences = getSharedPreferences("config_theme", MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor = savePreferences.edit();
        ObjEditor.putString("theme", key);
        ObjEditor.commit();
        //cambiamos el color de los elementos segun los colores de nuestra preferencia
        back.setBackgroundColor(Color.parseColor(c1));
        boton1.setBackgroundColor(Color.parseColor(c2));
        boton2.setBackgroundColor(Color.parseColor(c2));
        boton3.setBackgroundColor(Color.parseColor(c2));
        boton4.setBackgroundColor(Color.parseColor(c2));
        boton1.setTextColor(Color.parseColor(c1));
        boton2.setTextColor(Color.parseColor(c1));
        boton3.setTextColor(Color.parseColor(c1));
        boton4.setTextColor(Color.parseColor(c1));
        nombre.setBackgroundColor(Color.parseColor(c2));
        direccion.setBackgroundColor(Color.parseColor(c2));
        email.setBackgroundColor(Color.parseColor(c2));
        telefono.setBackgroundColor(Color.parseColor(c2));
        nombre.setTextColor(Color.parseColor(c1));
        direccion.setTextColor(Color.parseColor(c1));
        email.setTextColor(Color.parseColor(c1));
        telefono.setTextColor(Color.parseColor(c1));
        labelnombre.setBackgroundColor(Color.parseColor(c1));
        labeldireccion.setBackgroundColor(Color.parseColor(c1));
        labelemail.setBackgroundColor(Color.parseColor(c1));
        labeltelefono.setBackgroundColor(Color.parseColor(c1));
        labelnombre.setTextColor(Color.parseColor(c2));
        labeldireccion.setTextColor(Color.parseColor(c2));
        labelemail.setTextColor(Color.parseColor(c2));
        labeltelefono.setTextColor(Color.parseColor(c2));
        lista.setBackgroundColor(Color.parseColor(c2));
        change_theme.setTextColor(Color.parseColor(c2));
    }
    //funcion que nos permite caragar el tema
    public void loadTheme() {
        SharedPreferences loadPreferences = getSharedPreferences("config_theme", MODE_PRIVATE);
        String actualTheme = loadPreferences.getString("theme", "DEFAULT");
        //dependiendo del tema actual que este se actualizara al otro inmediatamente
        if(actualTheme.equals("DEFAULT")) {
            //si esta el tema claro cargara el oscuro
            updateTheme("DEFAULT", "#90EE90", "#8d4925","#FFFFFF","#FF3700B3");
        } else if(actualTheme.equals("DARK")) {
            //si esta en el modo oscuro cargara el claro
            updateTheme("DARK", "#212121", "#FFFFFF","#FFFFFF","#37474f");
            change_theme.setChecked(true);
        }
    }
    //creacion del menu
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menus,menu);
        return false;
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            //cierre  de sesion
            case R.id.inicio:
                mAuth.signOut();
                Intent intent = new Intent(Operaciones.this, Login.class);
                startActivity(intent);

                return true;
            //vacia el contenido de la lista
            case R.id.limpiardatos:
                ArrayAdapter<String> adapter;
                ArrayList<String> list =new ArrayList<String>();
                ListView lista= findViewById(R.id.listaNombres);
                adapter = new ArrayAdapter<>(Operaciones.this, android.R.layout.simple_list_item_1,list);
                lista.setAdapter(adapter);
                adapter.clear();
                adapter.notifyDataSetChanged();

                return true;

            default: return super.onOptionsItemSelected(item);

        }

    }
}