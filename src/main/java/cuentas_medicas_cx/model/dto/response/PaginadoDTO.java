package cuentas_medicas_cx.model.dto.response;

import org.springframework.data.domain.Page;

import java.util.List;

public class PaginadoDTO<T> {
    private List<T> contenido;
    private int paginaActual;
    private int tamanoPagina;
    private long totalElementos;
    private int totalPaginas;
    private boolean ultimo;
    private boolean primero;

    public PaginadoDTO() {}

    public PaginadoDTO(Page<T> page) {
        this.contenido = page.getContent();
        this.paginaActual = page.getNumber();
        this.tamanoPagina = page.getSize();
        this.totalElementos = page.getTotalElements();
        this.totalPaginas = page.getTotalPages();
        this.ultimo = page.isLast();
        this.primero = page.isFirst();
    }

    public List<T> getContenido() { return contenido; }
    public void setContenido(List<T> contenido) { this.contenido = contenido; }
    public int getPaginaActual() { return paginaActual; }
    public void setPaginaActual(int paginaActual) { this.paginaActual = paginaActual; }
    public int getTamanoPagina() { return tamanoPagina; }
    public void setTamanoPagina(int tamanoPagina) { this.tamanoPagina = tamanoPagina; }
    public long getTotalElementos() { return totalElementos; }
    public void setTotalElementos(long totalElementos) { this.totalElementos = totalElementos; }
    public int getTotalPaginas() { return totalPaginas; }
    public void setTotalPaginas(int totalPaginas) { this.totalPaginas = totalPaginas; }
    public boolean isUltimo() { return ultimo; }
    public void setUltimo(boolean ultimo) { this.ultimo = ultimo; }
    public boolean isPrimero() { return primero; }
    public void setPrimero(boolean primero) { this.primero = primero; }
}
