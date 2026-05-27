package FernandoDiaz;

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

import com.example.healthmanager.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

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

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                if (selected) {
                    showPopupMenu(widget, date);
                }
            }
        });
    }

    private void showPopupMenu(View view, CalendarDay date) {
        // Aplicamos el estilo personalizado
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
        // En una implementación real, aquí decidirías si es un INSERT o un UPDATE
        saveEventToDatabase(date, title, description);
        calendarView.clearSelection();
    }

    @Override
    public void onEditEvent(Event event) {
        // Al seleccionar un evento de la lista, abrimos el editor con sus datos
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
