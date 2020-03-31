package cu.tecnomatica.android.cupet_servicentros.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Auxiliar
{
    @Id(autoincrement = true)
    private Long idauxiliar;

    private boolean carga_inicial;

    @Generated(hash = 639511861)
    public Auxiliar(Long idauxiliar, boolean carga_inicial) {
        this.idauxiliar = idauxiliar;
        this.carga_inicial = carga_inicial;
    }

    @Generated(hash = 901845029)
    public Auxiliar() {
    }

    public Long getIdauxiliar() {
        return this.idauxiliar;
    }

    public void setIdauxiliar(Long idauxiliar) {
        this.idauxiliar = idauxiliar;
    }

    public boolean getCarga_inicial() {
        return this.carga_inicial;
    }

    public void setCarga_inicial(boolean carga_inicial) {
        this.carga_inicial = carga_inicial;
    }
}
