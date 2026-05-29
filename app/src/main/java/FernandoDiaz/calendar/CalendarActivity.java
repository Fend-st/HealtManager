package FernandoDiaz.calendar;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.HashSet;
import java.util.Locale;

import FernandoDiaz.crono.Cronometro;

/**
 * Actividad principal del calendario que gestiona la visualización de días,
 * la selección de fechas y la apertura de diálogos para crear o listar eventos.
 * este es el controlador principal del modulo calendario.
 * @author Fernando diaz
 */
public class CalendarActivity extends AppCompatActivity implements 
        EventDialogFragment.OnEventSavedListener,
        EventListDialogFragment.OnEventActionListener {

    /** Vista del calendario de la librería MaterialCalendarView. */
    private MaterialCalendarView calendarView;
    /** Gestor de la base de datos para realizar operaciones CRUD.
     * nota: el modulo calendario usa su propio objeto gestorBD
     * esto no interfiere con otras operaciones, no borrar esta linea por error*/

    private GestorBD gbd;

    /**
     * Se llama al crear la actividad. Configura la interfaz, el calendario y la navegación.
     *
     * @param savedInstanceState Si la actividad se recrea, contiene los datos guardados.
     */
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

        updateCalendarDecorators();
    }

    /**
     * Se ejecuta cuando la actividad vuelve a estar visible. 
     * Refresca el estado de la navegación y los decoradores del calendario.
     */
    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_calendar);
        }
        updateCalendarDecorators();
    }

    /**
     * Consulta la base de datos para obtener las fechas con eventos y actualiza los indicadores visuales.
     */
    private void updateCalendarDecorators() {
        SQLiteDatabase db = gbd.getReadableDatabase();
        HashSet<CalendarDay> dates = new HashSet<>();
        
        Cursor cursor = db.rawQuery("SELECT DISTINCT " + GestorBD.DIA_FECHA + " FROM " + GestorBD.TABLA_DIA, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String dateStr = cursor.getString(0);
                try {
                    String[] parts = dateStr.split("-");
                    int year = Integer.parseInt(parts[0]);
                    int month = Integer.parseInt(parts[1]) - 1;
                    int day = Integer.parseInt(parts[2]);
                    dates.add(CalendarDay.from(year, month, day));
                } catch (Exception ignored) {}
            }
            cursor.close();
        }
        db.close();

        calendarView.removeDecorators();
        calendarView.addDecorator(new EventDecorator(Color.parseColor("#FF4081"), dates));
    }

    /**
     * Muestra un menú emergente con las opciones de añadir evento o editar eventos existentes.
     *
     * @param view Vista de anclaje para el menú emergente.
     * @param date Fecha seleccionada en el calendario.
     */
    private void showPopupMenu(View view, CalendarDay date) {
        ContextThemeWrapper contextWrapper = new ContextThemeWrapper(this, R.style.CustomPopupMenu);
        PopupMenu popupMenu = new PopupMenu(contextWrapper, view);
        popupMenu.getMenuInflater().inflate(R.menu.calendar_popup_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.action_add_event) {
                    showEventDialog(date, null, null, null);
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

    /**
     * Abre el diálogo que muestra la lista de eventos para una fecha específica.
     *
     * @param date Fecha para la cual mostrar los eventos.
     */
    private void showEventListDialog(CalendarDay date) {
        EventListDialogFragment listDialog = EventListDialogFragment.newInstance(date);
        listDialog.setOnEventActionListener(this);
        listDialog.show(getSupportFragmentManager(), "EventListDialogFragment");
    }

    /**
     * Abre el diálogo para crear o editar la información de un evento.
     *
     * @param date        Fecha del evento.
     * @param eventId     Identificador del evento (null para nuevos).
     * @param title       Título actual (null para nuevos).
     * @param description Descripción actual (null para nuevos).
     */
    private void showEventDialog(CalendarDay date, @Nullable String eventId, @Nullable String title, @Nullable String description) {
        EventDialogFragment dialog;
        if (title == null) {
            dialog = EventDialogFragment.newInstance(date);
        } else {
            dialog = EventDialogFragment.newInstance(date, eventId, title, description);
        }
        dialog.setOnEventSavedListener(this);
        dialog.show(getSupportFragmentManager(), "EventDialogFragment");
    }

    /**
     * Método de callback invocado cuando se guarda un evento desde el diálogo.
     *
     * @param date        Fecha del evento.
     * @param title       Título ingresado.
     * @param description Descripción ingresada.
     * @param eventId     ID del evento (null si es nuevo).
     */
    @Override
    public void onEventSaved(CalendarDay date, String title, String description, @Nullable String eventId) {
        if (eventId == null) {
            saveEventToDatabase(date, title, description);
        } else {
            updateEventInDatabase(eventId, title, description);
        }
        calendarView.clearSelection();
        updateCalendarDecorators();
    }

    /**
     * Método de callback invocado desde el diálogo de lista para editar un evento seleccionado.
     *
     * @param event Objeto de tipo Event que se desea editar.
     */
    @Override
    public void onEditEvent(Event event) {
        showEventDialog(calendarView.getSelectedDate(), event.getId(), event.getTitle(), event.getDescription());
    }

    /**
     * Inserta un nuevo evento en la base de datos para la fecha proporcionada.
     *
     * @param date        Fecha del evento.
     * @param title       Título del evento.
     * @param description Descripción del evento.
     */
    private void saveEventToDatabase(CalendarDay date, String title, String description) {
        SQLiteDatabase db = gbd.getWritableDatabase();
        
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

        ContentValues ev = new ContentValues();
        ev.put(GestorBD.EVENTO_NOMBRE, title);
        ev.put(GestorBD.EVENTO_DESCRIPCION, description);
        ev.put(GestorBD.EVENTO_REPITE, 0);
        ev.put(GestorBD.EVENTO_ID_USUARIO, idUsuario);

        long idEvento = db.insert(GestorBD.TABLA_EVENTO, null, ev);

        if (idEvento != -1) {
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
     * Actualiza un evento existente en la base de datos.
     *
     * @param eventId     Identificador único del evento.
     * @param title       Nuevo título.
     * @param description Nueva descripción.
     */
    private void updateEventInDatabase(String eventId, String title, String description) {
        SQLiteDatabase db = gbd.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(GestorBD.EVENTO_NOMBRE, title);
        cv.put(GestorBD.EVENTO_DESCRIPCION, description);

        int rows = db.update(GestorBD.TABLA_EVENTO, cv, GestorBD.EVENTO_ID + " = ?", new String[]{eventId});
        if (rows > 0) {
            Toast.makeText(this, "Evento actualizado", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    /**
     * Clase decoradora para añadir indicadores visuales (puntos) a días específicos en el calendario.
     */
    private static class EventDecorator implements DayViewDecorator {
        /** Color del punto indicador. */
        private final int color;
        /** Conjunto de fechas que deben ser decoradas. */
        private final HashSet<CalendarDay> dates;

        /**
         * Constructor del decorador.
         *
         * @param color Color del indicador.
         * @param dates Fechas con eventos.
         */
        public EventDecorator(int color, HashSet<CalendarDay> dates) {
            this.color = color;
            this.dates = dates;
        }

        /**
         * Determina si un día dado debe ser decorado.
         *
         * @param day Día a evaluar.
         * @return true si el día está en el conjunto de fechas, false en caso contrario.
         */
        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        /**
         * Aplica la decoración a la celda del día.
         *
         * @param view Facade para configurar la apariencia.
         */
        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new DotSpan(8, color));
        }
    }
}
