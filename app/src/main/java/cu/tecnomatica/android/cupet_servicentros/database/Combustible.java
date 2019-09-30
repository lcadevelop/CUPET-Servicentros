package cu.tecnomatica.android.cupet_servicentros.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Combustible
{
    @Id(autoincrement = true)
    private Long idcombustible;

    private String nombre;
    private String codigo;
    private boolean existencia;
    private String fecha_abastecido;
    private String cantidad_abastecido;
    @Generated(hash = 67726392)
    public Combustible(Long idcombustible, String nombre, String codigo,
            boolean existencia, String fecha_abastecido,
            String cantidad_abastecido) {
        this.idcombustible = idcombustible;
        this.nombre = nombre;
        this.codigo = codigo;
        this.existencia = existencia;
        this.fecha_abastecido = fecha_abastecido;
        this.cantidad_abastecido = cantidad_abastecido;
    }
    @Generated(hash = 1402177194)
    public Combustible() {
    }
    public Long getIdcombustible() {
        return this.idcombustible;
    }
    public void setIdcombustible(Long idcombustible) {
        this.idcombustible = idcombustible;
    }
    public String getNombre() {
        return this.nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getCodigo() {
        return this.codigo;
    }
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    public boolean getExistencia() {
        return this.existencia;
    }
    public void setExistencia(boolean existencia) {
        this.existencia = existencia;
    }
    public String getFecha_abastecido() {
        return this.fecha_abastecido;
    }
    public void setFecha_abastecido(String fecha_abastecido) {
        this.fecha_abastecido = fecha_abastecido;
    }
    public String getCantidad_abastecido() {
        return this.cantidad_abastecido;
    }
    public void setCantidad_abastecido(String cantidad_abastecido) {
        this.cantidad_abastecido = cantidad_abastecido;
    }
}
