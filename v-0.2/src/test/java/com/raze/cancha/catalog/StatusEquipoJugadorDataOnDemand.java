package com.raze.cancha.catalog;
import com.raze.cancha.domain.UsuarioDataOnDemand;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.roo.addon.dod.RooDataOnDemand;
import org.springframework.stereotype.Component;

@Component
@Configurable
@RooDataOnDemand(entity = StatusEquipoJugador.class)
public class StatusEquipoJugadorDataOnDemand {

	private Random rnd = new SecureRandom();

	private List<StatusEquipoJugador> data;

	@Autowired
    UsuarioDataOnDemand usuarioDataOnDemand;

	public StatusEquipoJugador getNewTransientStatusEquipoJugador(int index) {
        StatusEquipoJugador obj = new StatusEquipoJugador();
        setActivo(obj, index);
        setDescripcion(obj, index);
        setFechaCreacion(obj, index);
        setFechaModificacion(obj, index);
        setNombreStatusEquipoJugador(obj, index);
        return obj;
    }

	public void setActivo(StatusEquipoJugador obj, int index) {
        Boolean activo = Boolean.TRUE;
        obj.setActivo(activo);
    }

	public void setDescripcion(StatusEquipoJugador obj, int index) {
        String descripcion = "descripcion_" + index;
        obj.setDescripcion(descripcion);
    }

	public void setFechaCreacion(StatusEquipoJugador obj, int index) {
        Date fechaCreacion = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), Calendar.getInstance().get(Calendar.SECOND) + new Double(Math.random() * 1000).intValue()).getTime();
        obj.setFechaCreacion(fechaCreacion);
    }

	public void setFechaModificacion(StatusEquipoJugador obj, int index) {
        Date fechaModificacion = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), Calendar.getInstance().get(Calendar.SECOND) + new Double(Math.random() * 1000).intValue()).getTime();
        obj.setFechaModificacion(fechaModificacion);
    }

	public void setNombreStatusEquipoJugador(StatusEquipoJugador obj, int index) {
        String nombreStatusEquipoJugador = "nombreStatusEquipoJugador_" + index;
        obj.setNombreStatusEquipoJugador(nombreStatusEquipoJugador);
    }

	public StatusEquipoJugador getSpecificStatusEquipoJugador(int index) {
        init();
        if (index < 0) {
            index = 0;
        }
        if (index > (data.size() - 1)) {
            index = data.size() - 1;
        }
        StatusEquipoJugador obj = data.get(index);
        Long id = obj.getId();
        return StatusEquipoJugador.findStatusEquipoJugador(id);
    }

	public StatusEquipoJugador getRandomStatusEquipoJugador() {
        init();
        StatusEquipoJugador obj = data.get(rnd.nextInt(data.size()));
        Long id = obj.getId();
        return StatusEquipoJugador.findStatusEquipoJugador(id);
    }

	public boolean modifyStatusEquipoJugador(StatusEquipoJugador obj) {
        return false;
    }

	public void init() {
        int from = 0;
        int to = 10;
        data = StatusEquipoJugador.findStatusEquipoJugadorEntries(from, to);
        if (data == null) {
            throw new IllegalStateException("Find entries implementation for 'StatusEquipoJugador' illegally returned null");
        }
        if (!data.isEmpty()) {
            return;
        }
        
        data = new ArrayList<StatusEquipoJugador>();
        for (int i = 0; i < 10; i++) {
            StatusEquipoJugador obj = getNewTransientStatusEquipoJugador(i);
            try {
                obj.persist();
            } catch (final ConstraintViolationException e) {
                final StringBuilder msg = new StringBuilder();
                for (Iterator<ConstraintViolation<?>> iter = e.getConstraintViolations().iterator(); iter.hasNext();) {
                    final ConstraintViolation<?> cv = iter.next();
                    msg.append("[").append(cv.getRootBean().getClass().getName()).append(".").append(cv.getPropertyPath()).append(": ").append(cv.getMessage()).append(" (invalid value = ").append(cv.getInvalidValue()).append(")").append("]");
                }
                throw new IllegalStateException(msg.toString(), e);
            }
            obj.flush();
            data.add(obj);
        }
    }
}
