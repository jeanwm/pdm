package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar os campos
        editTextEmail = findViewById(R.id.editTextTextEmailAddress);
        editTextPassword = findViewById(R.id.editTextTextPassword);
        Button buttonLogin = findViewById(R.id.button2);

        // Configurar o botão de login
        buttonLogin.setOnClickListener(v -> realizarLogin());
    }

    private void realizarLogin() {
        String email = editTextEmail.getText().toString();
        String senha = editTextPassword.getText().toString();

        // Validações básicas
        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificação simples de login, em uma aplicação real seria melhor mudar isso
        if (email.equals("admin@email.com") && senha.equals("123456")) {
            Intent intent = new Intent(MainActivity.this, MenuAdm.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "E-mail ou senha incorretos!", Toast.LENGTH_SHORT).show();
        }
    }
}