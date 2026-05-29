package FernandoDiaz.calendar;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.healthmanager.GestorBD;
import com.example.healthmanager.MainActivity;
import com.example.healthmanager.R;
import com.example.healthmanager.ResumenActividadActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.Locale;

import FernandoDiaz.crono.Cronometro;

/**
 * Actividad principal del calendario que gestiona la visualización de días,
 * la selección de fechas y la apertura de diálogos para crear o listar eventos.
 * Implementa interfaces para reaccionar a la creación y edición de eventos.
 */
public class CalendarActivity extends AppCompatActivity implements 
        EventDialogFragment.OnEventSavedListener,
        EventListDialogFragment.OnEventActionListener {

    private MaterialCalendarView calendarView;
    private GestorBD gbd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calendar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        gbd = new GestorBD(this);
        calendarView = findViewById(R.id.calendarView);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_calendar);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_calendar) {
                return true;
            } else if (itemId == R.id.nav_timer) {
                Intent intent = new Intent(this, Cronometro.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_summary) {
                Intent intent = new Intent(this, ResumenActividadActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            }
            return false;
        });

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                if (selected) {
                    showPopupMenu(widget, date);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_calendar);
        }
    }

    // muestra los popups de editar y crear evento
    private void showPopupMenu(View view, CalendarDay date) {
        // estilo personalizado
        ContextThemeWrapper contextWrapper = new ContextThemeWrapper(this, R.style.CustomPopupMenu);
        PopupMenu popupMenu = new PopupMenu(contextWrapper, view);
        popupMenu.getMenuInflater().inflate(R.menu.calendar_popup_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.action_add_event) {
                    showEventDialog(date, null, null);
                    return true;
                } else if (id == R.id.action_edit_events) {
                    showEventListDialog(date);
                    return true;
                }
                return false;
            }
        });

        popupMenu.show();
    }

    private void showEventListDialog(CalendarDay date) {
        EventListDialogFragment listDialog = EventListDialogFragment.newInstance(date);
        listDialog.setOnEventActionListener(this);
        listDialog.show(getSupportFragmentManager(), "EventListDialogFragment");
    }

    private void showEventDialog(CalendarDay date, String title, String description) {
        EventDialogFragment dialog;
        if (title == null) {
            dialog = EventDialogFragment.newInstance(date);
        } else {
            dialog = EventDialogFragment.newInstance(date, title, description);
        }
        dialog.setOnEventSavedListener(this);
        dialog.show(getSupportFragmentManager(), "EventDialogFragment");
    }

    @Override
    public void onEventSaved(CalendarDay date, String title, String description) {
        saveEventToDatabase(date, title, description);
        calendarView.clearSelection();
    }

    @Override
    public void onEditEvent(Event event) {
        // Al seleccionar un evento de la lista, se abre el editor con sus datos
        showEventDialog(calendarView.getSelectedDate(), event.getTitle(), event.getDescription());
    }

    /**
     * Inserta un nuevo evento en la base de datos de forma manual, respetando que GestorBD no se puede modificar.
     */
    private void saveEventToDatabase(CalendarDay date, String title, String description) {
        SQLiteDatabase db = gbd.getWritableDatabase();
        
        // 1. Obtener ID del primer usuario disponible
        int idUsuario = -1;
        Cursor cur = db.rawQuery("SELECT " + GestorBD.USUARIO_ID + " FROM " + GestorBD.TABLA_USUARIO + " LIMIT 1", null);
        if (cur.moveToFirst()) {
            idUsuario = cur.getInt(0);
        }
        cur.close();

        if (idUsuario == -1) {
            Toast.makeText(this, "Debe registrar un usuario primero", Toast.LENGTH_SHORT).show();
            db.close();
            return;
        }

        // 2. Insertar en TABLA_EVENTO
        ContentValues ev = new ContentValues();
        ev.put(GestorBD.EVENTO_NOMBRE, title);
        ev.put(GestorBD.EVENTO_DESCRIPCION, description);
        ev.put(GestorBD.EVENTO_REPITE, 0);
        ev.put(GestorBD.EVENTO_ID_USUARIO, idUsuario);

        long idEvento = db.insert(GestorBD.TABLA_EVENTO, null, ev);

        if (idEvento != -1) {
            // 3. Insertar en TABLA_DIA (Relación fecha-evento)
            // Usamos formato ISO (YYYY-MM-DD) para consistencia en búsquedas
            String dateFormatted = String.format(Locale.getDefault(), "%04d-%02d-%02d", 
                    date.getYear(), date.getMonth() + 1, date.getDay());
            
            ContentValues dv = new ContentValues();
            dv.put(GestorBD.DIA_FECHA, dateFormatted);
            dv.put(GestorBD.DIA_EMOCION, "Normal");
            dv.put(GestorBD.DIA_ID_EVENTO, (int) idEvento);

            if (db.insert(GestorBD.TABLA_DIA, null, dv) != -1) {
                Toast.makeText(this, "Evento guardado con éxito", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al programar el día", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Error al crear el evento", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    /**
     * MÉTODO PLACEHOLDER: Lógica para ACTUALIZAR un evento existente.
     */
    private void updateEventInDatabase(CalendarDay date, String title, String description) {
        String dateFormatted = date.getDay() + "/" + (date.getMonth() + 1) + "/" + date.getYear();
        Toast.makeText(this, "EVENTO ACTUALIZADO: " + title + " (" + dateFormatted + ")", Toast.LENGTH_SHORT).show();
    }
}
