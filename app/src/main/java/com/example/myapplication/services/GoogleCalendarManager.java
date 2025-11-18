package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.Collections;
import java.util.Date;

public class GoogleCalendarManager {
    private static final String TAG = "GoogleCalendarManager";
    private Context context;
    private Calendar calendarService;

    public GoogleCalendarManager(Context context) {
        this.context = context;
    }

    // Configurar o cliente de login do Google
    public GoogleSignInClient getGoogleSignInClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(CalendarScopes.CALENDAR))
                .build();

        return GoogleSignIn.getClient(context, gso);
    }

    // Inicializar o serviço do Calendar
    public void initializeCalendarService(GoogleSignInAccount account) {
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                context, Collections.singleton(CalendarScopes.CALENDAR));
        credential.setSelectedAccount(account.getAccount());

        calendarService = new Calendar.Builder(
                AndroidHttp.newCompatibleTransport(),
                new GsonFactory(),
                credential)
                .setApplicationName("Cinema Estudantil")
                .build();
    }

    // Criar evento no Google Calendar
    public void criarEventoNoCalendar(Sessao sessao, String tituloFilme, String nomeLocal) {
        if (calendarService == null) {
            Log.e(TAG, "Calendar service não inicializado");
            return;
        }

        try {
            // Converter data e hora para o formato do Calendar
            Event event = new Event()
                    .setSummary("Sessão: " + tituloFilme)
                    .setLocation(nomeLocal)
                    .setDescription("Sessão de cinema criada pelo app Cinema Estudantil");

            // Configurar data e hora de início
            Date dataSessao = sessao.getData();
            String horaSessao = sessao.getHora();
            
            LocalDateTime startDateTime = combineDataHora(dataSessao, horaSessao);
            EventDateTime start = new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(startDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
            event.setStart(start);

            // Configurar data e hora de fim (assumindo 2 horas de duração)
            LocalDateTime endDateTime = startDateTime.plusHours(2);
            EventDateTime end = new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(endDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
            event.setEnd(end);

            // Inserir o evento
            String calendarId = "primary";
            Event createdEvent = calendarService.events().insert(calendarId, event).execute();

            Log.i(TAG, "Evento criado: " + createdEvent.getId());
            
        } catch (Exception e) {
            Log.e(TAG, "Erro ao criar evento no Calendar", e);
        }
    }

    // Método auxiliar para combinar data e hora
    private LocalDateTime combineDataHora(Date data, String hora) {
        LocalDateTime dataLocal = LocalDateTime.ofInstant(data.toInstant(), ZoneId.systemDefault());
        
        // Parse da hora no formato "HH:mm"
        String[] partesHora = hora.split(":");
        int horas = Integer.parseInt(partesHora[0]);
        int minutos = Integer.parseInt(partesHora[1]);
        
        return dataLocal.withHour(horas).withMinute(minutos).withSecond(0);
    }

    // Verificar se o usuário está logado
    public boolean isUserLoggedIn() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        return account != null;
    }

    // Obter conta logada
    public GoogleSignInAccount getLoggedInAccount() {
        return GoogleSignIn.getLastSignedInAccount(context);
    }
}
