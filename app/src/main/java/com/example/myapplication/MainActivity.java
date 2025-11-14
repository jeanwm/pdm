package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realizarLogin();
            }
        });

        // Limpar o texto padrão quando o usuário clicar nos campos
        editTextEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && editTextEmail.getText().toString().equals("E-mail")) {
                    editTextEmail.setText("");
                }
            }
        });

        editTextPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && editTextPassword.getText().toString().equals("Senha")) {
                    editTextPassword.setText("");
                    editTextPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                            android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
    }

    private void realizarLogin() {
        String email = editTextEmail.getText().toString();
        String senha = editTextPassword.getText().toString();

        // Validações básicas
        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificação simples de login (em uma aplicação real, isso seria mais complexo)
        if (email.equals("admin@email.com") && senha.equals("123456")) {
            // Login bem-sucedido - ir para o menu administrativo
            Intent intent = new Intent(MainActivity.this, MenuAdm.class);
            startActivity(intent);
            // Opcional: finish(); // Se quiser fechar a tela de login
        } else {
            Toast.makeText(this, "E-mail ou senha incorretos!", Toast.LENGTH_SHORT).show();
        }
    }
}