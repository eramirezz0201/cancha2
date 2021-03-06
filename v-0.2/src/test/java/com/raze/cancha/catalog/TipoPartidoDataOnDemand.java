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
@RooDataOnDemand(entity = TipoPartido.class)
public class TipoPartidoDataOnDemand {

	private Random rnd = new SecureRandom();

	private List<TipoPartido> data;

	@Autowired
    UsuarioDataOnDemand usuarioDataOnDemand;

	public TipoPartido getNewTransientTipoPartido(int index) {
        TipoPartido obj = new TipoPartido();
        setActivo(obj, index);
        setDescripcion(obj, index);
        setFechaCreacion(obj, index);
        setFechaModificacion(obj, index);
        setNombreTipoPartido(obj, index);
        return obj;
    }

	public void setActivo(TipoPartido obj, int index) {
        Boolean activo = Boolean.TRUE;
        obj.setActivo(activo);
    }

	public void setDescripcion(TipoPartido obj, int index) {
        String descripcion = "descripcion_" + index;
        obj.setDescripcion(descripcion);
    }

	public void setFechaCreacion(TipoPartido obj, int index) {
        Date fechaCreacion = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), Calendar.getInstance().get(Calendar.SECOND) + new Double(Math.random() * 1000).intValue()).getTime();
        obj.setFechaCreacion(fechaCreacion);
    }

	public void setFechaModificacion(TipoPartido obj, int index) {
        Date fechaModificacion = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), Calendar.getInstance().get(Calendar.SECOND) + new Double(Math.random() * 1000).intValue()).getTime();
        obj.setFechaModificacion(fechaModificacion);
    }

	public void setNombreTipoPartido(TipoPartido obj, int index) {
        String nombreTipoPartido = "nombreTipoPartido_" + index;
        obj.setNombreTipoPartido(nombreTipoPartido);
    }

	public TipoPartido getSpecificTipoPartido(int index) {
        init();
        if (index < 0) {
            index = 0;
        }
        if (index > (data.size() - 1)) {
            index = data.size() - 1;
        }
        TipoPartido obj = data.get(index);
        Long id = obj.getId();
        return TipoPartido.findTipoPartido(id);
    }

	public TipoPartido getRandomTipoPartido() {
        init();
        TipoPartido obj = data.get(rnd.nextInt(data.size()));
        Long id = obj.getId();
        return TipoPartido.findTipoPartido(id);
    }

	public boolean modifyTipoPartido(TipoPartido obj) {
        return false;
    }

	public void init() {
        int from = 0;
        int to = 10;
        data = TipoPartido.findTipoPartidoEntries(from, to);
        if (data == null) {
            throw new IllegalStateException("Find entries implementation for 'TipoPartido' illegally returned null");
        }
        if (!data.isEmpty()) {
            return;
        }
        
        data = new ArrayList<TipoPartido>();
        for (int i = 0; i < 10; i++) {
            TipoPartido obj = getNewTransientTipoPartido(i);
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
