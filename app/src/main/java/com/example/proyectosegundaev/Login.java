package com.example.proyectosegundaev;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {
    //variables para la autenticacion,de los elementos de la ventana y para las validaciones
    private static final String TAG = "EmailPassword";
    private FirebaseAuth usuario;
    private ConstraintLayout back;
    private Button boton1,boton2;
    private Switch change_theme;
    private EditText registroemail;
    private TextView emaillabel,contraseñalabel,titulo;
    private  EditText editcontraseña;
    Pattern regexemail=Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        usuario = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login);
        Button registrar = findViewById(R.id.Registro);
        registroemail = findViewById(R.id.email2);
        editcontraseña = findViewById(R.id.contraseña);
        //funcion que al clixkar sobre el boton registrar comprobara si los datos son validos y nos registrara
        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = registroemail.getText().toString();
                String contrasena = editcontraseña.getText().toString();
                Matcher matcheremail=regexemail.matcher(email);
                if(matcheremail.find()) {
                    createAccount(email, contrasena);
                }
                else{
                    Toast.makeText(Login.this, "Credenciales incorrectas",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        //funcion que al clickar sobre el boton iniciar sesion comprobara si los datos existen e iniciara la sesion
        Button loguearse = findViewById(R.id.Login);
        loguearse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = registroemail.getText().toString();
                String contrasena = editcontraseña.getText().toString();

                    signIn(email, contrasena);

            }
        });


    //elementos de la ventana para cambiar el estilo de estos
        back = (ConstraintLayout) findViewById(R.id.fondo);
        boton1= (Button) findViewById(R.id.Registro);
        boton2= (Button) findViewById(R.id.Login);
        change_theme = (Switch) findViewById(R.id.estilos);
        registroemail = findViewById(R.id.email2);
        editcontraseña = findViewById(R.id.contraseña);
        emaillabel=findViewById(R.id.emaillabel2);
        contraseñalabel=findViewById(R.id.contraseñalabel);
        titulo=findViewById(R.id.titulo);
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
                    updateTheme("DEFAULT", "#90EE90", "#8d4925","#FFFFFF","#FF3700B3");                }
            }
        });

        loadTheme();
    }



    //esta funcion nos permitira crear la cuenta
    private void createAccount(String email, String password) {
        // [START create_user_with_email]
        usuario.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Inicio de sesion sastisfactorio
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = usuario.getCurrentUser();

                        } else {
                            // sesion no encontrada mensaje de error
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }
    //funcion que nos permite loguarse
    private void signIn(String email, String password) {
        usuario.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //si la cuenta existe
                        if (task.isSuccessful()) {
                            //Inicio de sesion satisfactorio
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = usuario.getCurrentUser();
                            Intent intent = new Intent(Login.this, Operaciones.class);
                            startActivity(intent);

                        } else {
                            // si falla mensaje de error.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
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
        boton1.setTextColor(Color.parseColor(c1));
        boton2.setTextColor(Color.parseColor(c1));
        registroemail.setBackgroundColor(Color.parseColor(c2));
        registroemail.setTextColor(Color.parseColor(c1));
        titulo.setBackgroundColor(Color.parseColor(c1));
        titulo.setTextColor(Color.parseColor(c2));
        editcontraseña.setBackgroundColor(Color.parseColor(c2));
        editcontraseña.setTextColor(Color.parseColor(c1));
        change_theme.setTextColor(Color.parseColor(c2));
        emaillabel.setTextColor(Color.parseColor(c2));
        contraseñalabel.setTextColor(Color.parseColor(c2));
        emaillabel.setBackgroundColor(Color.parseColor(c1));
        contraseñalabel.setBackgroundColor(Color.parseColor(c1));
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

}