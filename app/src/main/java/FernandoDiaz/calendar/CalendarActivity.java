package FernandoDiaz.calendar;

import android.content.Intent;
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

import com.example.healthmanager.MainActivity;
import com.example.healthmanager.R;
import com.example.healthmanager.ResumenActividadActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import FernandoDiaz.crono.Cronometro;

public class CalendarActivity extends AppCompatActivity implements 
        EventDialogFragment.OnEventSavedListener,
        EventListDialogFragment.OnEventActionListener {

    private MaterialCalendarView calendarView;

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

        calendarView = findViewById(R.id.calendarView);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_calendar);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_calendar) {
                return true;
            } else if (itemId == R.id.nav_timer) {
                startActivity(new Intent(this, Cronometro.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_summary) {
                startActivity(new Intent(this, ResumenActividadActivity.class));
                finish();
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
        // En la implementación real, aquí se decide  si es un INSERT o un UPDATE
        saveEventToDatabase(date, title, description);
        calendarView.clearSelection();
    }

    @Override
    public void onEditEvent(Event event) {
        // Al seleccionar un evento de la lista, se abre el editor con sus datos
        showEventDialog(calendarView.getSelectedDate(), event.getTitle(), event.getDescription());
    }

    /**
     * MÉTODO PLACEHOLDER: Lógica para INSERTAR un nuevo evento.
     */
    private void saveEventToDatabase(CalendarDay date, String title, String description) {
        String dateFormatted = date.getDay() + "/" + (date.getMonth() + 1) + "/" + date.getYear();
        Toast.makeText(this, "NUEVO EVENTO: " + title + " (" + dateFormatted + ")", Toast.LENGTH_SHORT).show();
    }

    /**
     * MÉTODO PLACEHOLDER: Lógica para ACTUALIZAR un evento existente.
     */
    private void updateEventInDatabase(CalendarDay date, String title, String description) {
        String dateFormatted = date.getDay() + "/" + (date.getMonth() + 1) + "/" + date.getYear();
        Toast.makeText(this, "EVENTO ACTUALIZADO: " + title + " (" + dateFormatted + ")", Toast.LENGTH_SHORT).show();
    }
}
