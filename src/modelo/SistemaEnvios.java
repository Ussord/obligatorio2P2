/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import modelo.dtos.ConsultaPorCliente;
import modelo.dtos.DatosPaquete;
import modelo.dtos.DetallePaquetesPorEstado;
import modelo.dtos.ReportePaquetesPorEstado;
import modelo.enums.Departamento;
import modelo.enums.EstadoPaquete;
import modelo.enums.TipoEvento;
import modelo.enums.Zona;
import persistencia.PersistenciaSistema;

/**
 *
 * @author Mauro
 */
@SuppressWarnings("java:S1200")
public class SistemaEnvios implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String ARCHIVO_SISTEMA = "SistemaEnvios.dat";
    private static final String ARCHIVO_TARIFAS = "Tarifas.txt";
    private static final String ARCHIVO_TRANSACCIONES = "Transacciones.log";
    private ArrayList<Cliente> clientes;
    private ArrayList<Funcionario> funcionarios;
    private ArrayList<Paquete> paquetes;
    private ArrayList<Envio> envios;
    private Tarifas tarifas;
    private LogTransacciones logTransacciones;
    private int proximoNumeroEnvio;
    private transient PropertyChangeSupport support;

    public SistemaEnvios() {
        this.clientes = new ArrayList<>();
        this.funcionarios = new ArrayList<>();
        this.paquetes = new ArrayList<>();
        this.envios = new ArrayList<>();
        this.tarifas = new Tarifas();
        this.logTransacciones = new LogTransacciones(ARCHIVO_TRANSACCIONES);
        this.proximoNumeroEnvio = 1;
        inicializarObserver();
        cargarTarifas();
    }

    public static SistemaEnvios cargarSistema(boolean usarDatosGuardados) {
        SistemaEnvios sistema;
        if (usarDatosGuardados && PersistenciaSistema.existeArchivo(ARCHIVO_SISTEMA)) {
            sistema = PersistenciaSistema.cargar(ARCHIVO_SISTEMA);
            sistema.asegurarComponentes();
        } else {
            sistema = new SistemaEnvios();
        }
        sistema.inicializarObserver();
        sistema.cargarTarifas();
        return sistema;
    }

    public void guardarSistema() {
        PersistenciaSistema.guardar(ARCHIVO_SISTEMA, this);
    }

    public void crearCliente(String nombre, String celular, String correoElectronico) {
        validarTexto(nombre, "El nombre del cliente no puede ser vacío.");
        validarTexto(celular, "El celular del cliente no puede ser vacío.");
        validarTexto(correoElectronico, "El correo electrónico del cliente no puede ser vacío.");
        validarNombreUnico(nombre, null);
        Cliente cliente = new Cliente(nombre.trim(), celular.trim(), correoElectronico.trim());
        clientes.add(cliente);
        logTransacciones.registrar("Ingreso de cliente " + cliente.getNombre());
        notificar(TipoEvento.CLIENTES);
        notificar(TipoEvento.LOG_TRANSACCIONES);
    }

    public void modificarCliente(Cliente cliente,
            String nombre,
            String celular,
            String correoElectronico) {
        validarClienteExistente(cliente);
        validarTexto(nombre, "El nombre del cliente no puede ser vacío.");
        validarTexto(celular, "El celular del cliente no puede ser vacío.");
        validarTexto(correoElectronico, "El correo electrónico del cliente no puede ser vacío.");
        validarNombreUnico(nombre, cliente);
        cliente.setNombre(nombre.trim());
        cliente.setCelular(celular.trim());
        cliente.setCorreoElectronico(correoElectronico.trim());
        logTransacciones.registrar("Modificación de cliente " + cliente.getNombre());
        notificar(TipoEvento.CLIENTES);
        notificar(TipoEvento.PAQUETES);
        notificar(TipoEvento.LOG_TRANSACCIONES);
    }

    public void crearFuncionario(String nombre,
            String celular,
            int numeroFuncionario,
            int anioIngreso) {
        validarTexto(nombre, "El nombre del funcionario no puede ser vacío.");
        validarTexto(celular, "El celular del funcionario no puede ser vacío.");
        validarNumeroFuncionario(numeroFuncionario);
        validarAnioIngreso(anioIngreso);
        validarNombreUnico(nombre, null);
        Funcionario funcionario = new Funcionario(
                nombre.trim(),
                celular.trim(),
                numeroFuncionario,
                anioIngreso
        );
        funcionarios.add(funcionario);
        logTransacciones.registrar("Ingreso de funcionario " + funcionario.getNombre());
        notificar(TipoEvento.FUNCIONARIOS);
        notificar(TipoEvento.LOG_TRANSACCIONES);
    }

    public void modificarFuncionario(Funcionario funcionario,
            String nombre,
            String celular,
            int numeroFuncionario,
            int anioIngreso) {
        validarFuncionarioExistente(funcionario);
        validarTexto(nombre, "El nombre del funcionario no puede ser vacío.");
        validarTexto(celular, "El celular del funcionario no puede ser vacío.");
        validarNumeroFuncionario(numeroFuncionario);
        validarAnioIngreso(anioIngreso);
        validarNombreUnico(nombre, funcionario);
        funcionario.setNombre(nombre.trim());
        funcionario.setCelular(celular.trim());
        funcionario.setNumeroFuncionario(numeroFuncionario);
        funcionario.setAnioIngreso(anioIngreso);
        logTransacciones.registrar("Modificación de funcionario " + funcionario.getNombre());
        notificar(TipoEvento.FUNCIONARIOS);
        notificar(TipoEvento.ENVIOS);
        notificar(TipoEvento.LOG_TRANSACCIONES);
    }

    public void actualizarTarifas(double porcentaje) {
        validarPorcentaje(porcentaje);
        tarifas.actualizarTarifas(porcentaje);
        tarifas.guardarEnArchivo(ARCHIVO_TARIFAS);
        logTransacciones.registrar("Actualización de tarifas con porcentaje " + porcentaje);
        notificar(TipoEvento.TARIFAS);
        notificar(TipoEvento.LOG_TRANSACCIONES);
    }

    public Paquete ingresarPaquete(DatosPaquete datos) {
        validarDatosIngresoPaquete(datos);
        validarIdentificadorPaqueteUnico(datos.getIdentificador());
        validarClienteExistente(datos.getCliente());
        int precio = calcularPrecio(datos.getDepartamentoDestino(), datos.getPesoGramos());
        Paquete paquete = new Paquete(datos, precio);
        paquetes.add(paquete);
        logTransacciones.registrar(
                "Ingreso de paquete de cliente " + datos.getCliente().getNombre()
        );
        notificar(TipoEvento.PAQUETES);
        notificar(TipoEvento.LOG_TRANSACCIONES);
        return paquete;
    }

    public Envio ingresarEnvio(Funcionario funcionario,
            LocalDate fechaEnvio,
            Zona zona,
            List<Paquete> paquetesSeleccionados) {
        validarFuncionarioExistente(funcionario);
        validarFecha(fechaEnvio, "La fecha del envío no puede ser null.");
        validarZona(zona);
        validarPaquetesPendientesDeZona(new ArrayList<>(paquetesSeleccionados), zona);
        Envio envio = new Envio(proximoNumeroEnvio, fechaEnvio, zona, funcionario);
        for (Paquete paquete : paquetesSeleccionados) {
            envio.agregarPaquete(paquete);
        }
        envios.add(envio);
        proximoNumeroEnvio++;
        logTransacciones.registrar("Ingreso de envío número " + envio.getNumero());
        notificar(TipoEvento.ENVIOS);
        notificar(TipoEvento.PAQUETES);
        notificar(TipoEvento.LOG_TRANSACCIONES);
        return envio;
    }

    public void registrarRecepcion(Envio envio, List<Paquete> paquetesEntregados) {
        validarEnvioExistente(envio);
        if (paquetesEntregados == null) {
            throw new IllegalArgumentException("La lista de paquetes entregados no puede ser null.");
        }
        validarPaquetesEntregados(envio, new ArrayList<>(paquetesEntregados));
        envio.registrarRecepcion(paquetesEntregados);
        logTransacciones.registrar("Registro de recepción del envío número " + envio.getNumero());
        notificar(TipoEvento.RECEPCIONES);
        notificar(TipoEvento.ENVIOS);
        notificar(TipoEvento.PAQUETES);
        notificar(TipoEvento.LOG_TRANSACCIONES);
    }

    public int calcularPrecio(Departamento departamentoDestino, int pesoGramos) {
        if (departamentoDestino == null) {
            throw new IllegalArgumentException("El departamento destino no puede ser null.");
        }
        if (pesoGramos <= 0) {
            throw new IllegalArgumentException("El peso debe ser mayor a cero.");
        }
        return tarifas.calcularPrecio(departamentoDestino, pesoGramos);
    }

    public List<Cliente> clientesOrdenadosPorNombre() {
        ArrayList<Cliente> ordenados = new ArrayList<>(clientes);
        ordenados.sort(Comparator.comparing(
                Cliente::getNombre,
                String.CASE_INSENSITIVE_ORDER
        ));
        return ordenados;
    }

    public List<Funcionario> funcionariosOrdenadosPorAnioIngresoDecreciente() {
        ArrayList<Funcionario> ordenados = new ArrayList<>(funcionarios);
        ordenados.sort(
                Comparator.comparingInt(Funcionario::getAnioIngreso).reversed()
        );
        return ordenados;
    }

    public List<Paquete> paquetesPendientesDeZona(Zona zona) {
        validarZona(zona);
        ArrayList<Paquete> resultado = new ArrayList<>();
        for (Paquete paquete : paquetes) {
            if (paquete.estaPendiente() && paquete.getZona() == zona) {
                resultado.add(paquete);
            }
        }
        return resultado;
    }

    public List<Envio> enviosOrdenadosPorNumeroDecreciente() {
        ArrayList<Envio> ordenados = new ArrayList<>(envios);
        ordenados.sort(
                Comparator.comparingInt(Envio::getNumero).reversed()
        );
        return ordenados;
    }

    public ReportePaquetesPorEstado paquetesPorEstado() {
        int[][] cantidades = new int[Zona.values().length][EstadoPaquete.values().length];
        for (Paquete paquete : paquetes) {
            Zona zona = paquete.getZona();
            EstadoPaquete estado = paquete.getEstado();
            cantidades[zona.ordinal()][estado.ordinal()]++;
        }
        return new ReportePaquetesPorEstado(cantidades);
    }

    public DetallePaquetesPorEstado detallePaquetesPorEstado(Zona zona, EstadoPaquete estado) {
        validarZona(zona);
        validarEstadoPaquete(estado);
        Set<Cliente> clientesDiferentes = new HashSet<>();
        EnumSet<Departamento> departamentosDestino = EnumSet.noneOf(Departamento.class);
        for (Paquete paquete : paquetes) {
            if (paquete.getZona() == zona && paquete.getEstado() == estado) {
                clientesDiferentes.add(paquete.getCliente());
                departamentosDestino.add(paquete.getDepartamentoDestino());
            }
        }
        return new DetallePaquetesPorEstado(
                clientesDiferentes.size(),
                new ArrayList<>(departamentosDestino)
        );
    }

    public DetallePaquetesPorEstado detallePaquetesPorZona(Zona zona) {
        validarZona(zona);
        Set<Cliente> clientesDiferentes = new HashSet<>();
        Set<Departamento> departamentosDestino = EnumSet.noneOf(Departamento.class);
        for (Paquete paquete : paquetes) {
            if (paquete.getZona() == zona) {
                clientesDiferentes.add(paquete.getCliente());
                departamentosDestino.add(paquete.getDepartamentoDestino());
            }
        }
        return new DetallePaquetesPorEstado(
                clientesDiferentes.size(),
                new ArrayList<>(departamentosDestino)
        );
    }

    public ConsultaPorCliente consultaPorCliente(Cliente cliente) {
        validarClienteExistente(cliente);
        int pendientes = 0;
        int enviados = 0;
        int recibidos = 0;
        for (Paquete paquete : paquetes) {
            if (paquete.getCliente() == cliente) {
                switch (paquete.getEstado()) {
                    case PENDIENTE ->
                        pendientes++;
                    case ENVIADO ->
                        enviados++;
                    case RECIBIDO ->
                        recibidos++;
                    default ->
                        throw new IllegalStateException("Estado de paquete no reconocido.");
                }
            }
        }
        return new ConsultaPorCliente(cliente, pendientes, enviados, recibidos);
    }

    public String leerLogTransacciones() {
        return logTransacciones.leer();
    }

    public void borrarLogTransacciones() {
        logTransacciones.borrar();
        notificar(TipoEvento.LOG_TRANSACCIONES);
    }

    public List<Cliente> getClientes() {
        return new ArrayList<>(clientes);
    }

    public List<Funcionario> getFuncionarios() {
        return new ArrayList<>(funcionarios);
    }

    public List<Paquete> getPaquetes() {
        return new ArrayList<>(paquetes);
    }

    public List<Envio> getEnvios() {
        return new ArrayList<>(envios);
    }

    public Tarifas getTarifas() {
        return tarifas;
    }

    public int getProximoNumeroEnvio() {
        return proximoNumeroEnvio;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listener != null) {
            support.addPropertyChangeListener(listener);
        }
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        if (listener != null) {
            support.removePropertyChangeListener(listener);
        }
    }

    private void inicializarObserver() {
        this.support = new PropertyChangeSupport(this);
    }

    private void notificar(TipoEvento tipo) {
        if (support != null && tipo != null) {
            support.firePropertyChange(tipo.name(), null, this);
        }
    }

    private void cargarTarifas() {
        tarifas.cargarDesdeArchivo(ARCHIVO_TARIFAS);
    }

    private void asegurarComponentes() {
        if (clientes == null) {
            clientes = new ArrayList<>();
        }
        if (funcionarios == null) {
            funcionarios = new ArrayList<>();
        }
        if (paquetes == null) {
            paquetes = new ArrayList<>();
        }
        if (envios == null) {
            envios = new ArrayList<>();
        }
        if (tarifas == null) {
            tarifas = new Tarifas();
        }
        if (logTransacciones == null) {
            logTransacciones = new LogTransacciones(ARCHIVO_TRANSACCIONES);
        }
        if (proximoNumeroEnvio <= 0) {
            proximoNumeroEnvio = calcularProximoNumeroEnvio();
        }
    }

    private int calcularProximoNumeroEnvio() {
        int maximo = 0;
        for (Envio envio : envios) {
            if (envio.getNumero() > maximo) {
                maximo = envio.getNumero();
            }
        }
        return maximo + 1;
    }

    private void validarDatosIngresoPaquete(DatosPaquete datos) {
        if (datos == null) {
            throw new IllegalArgumentException("Los datos del paquete no pueden ser null.");
        }
        validarTexto(datos.getIdentificador(), "El identificador del paquete no puede ser vacío.");
        validarFecha(datos.getFechaIngreso(), "La fecha de ingreso no puede ser null.");
        validarTexto(datos.getDestinatario(), "El destinatario no puede ser vacío.");
        validarTexto(datos.getDireccion(), "La dirección no puede ser vacía.");
        if (datos.getDepartamentoDestino() == null) {
            throw new IllegalArgumentException("El departamento destino no puede ser null.");
        }
        if (datos.getPesoGramos() <= 0) {
            throw new IllegalArgumentException("El peso debe ser mayor a cero.");
        }
    }

    private void validarNombreUnico(String nombre, Persona personaActual) {
        for (Cliente cliente : clientes) {
            if (cliente != personaActual && mismosNombres(cliente.getNombre(), nombre)) {
                throw new IllegalArgumentException("Ya existe una persona con ese nombre.");
            }
        }
        for (Funcionario funcionario : funcionarios) {
            if (funcionario != personaActual && mismosNombres(funcionario.getNombre(), nombre)) {
                throw new IllegalArgumentException("Ya existe una persona con ese nombre.");
            }
        }
    }

    private boolean mismosNombres(String nombreActual, String nombreNuevo) {
        return nombreActual != null
                && nombreNuevo != null
                && nombreActual.trim().equalsIgnoreCase(nombreNuevo.trim());
    }

    private void validarIdentificadorPaqueteUnico(String identificador) {
        for (Paquete paquete : paquetes) {
            if (paquete.getIdentificador().equalsIgnoreCase(identificador.trim())) {
                throw new IllegalArgumentException("Ya existe un paquete con ese identificador.");
            }
        }
    }

    private void validarPaquetesPendientesDeZona(ArrayList<Paquete> paquetesSeleccionados, Zona zona) {
        if (paquetesSeleccionados == null || paquetesSeleccionados.isEmpty()) {
            throw new IllegalArgumentException("El envío debe tener al menos un paquete.");
        }
        Set<Paquete> sinRepetidos = new HashSet<>();
        for (Paquete paquete : paquetesSeleccionados) {
            validarPaqueteExistente(paquete);
            if (!sinRepetidos.add(paquete)) {
                throw new IllegalArgumentException("No se puede seleccionar el mismo paquete más de una vez.");
            }
            if (!paquete.estaPendiente()) {
                throw new IllegalArgumentException("Todos los paquetes deben estar pendientes.");
            }
            if (paquete.getZona() != zona) {
                throw new IllegalArgumentException("Todos los paquetes deben pertenecer a la zona del envío.");
            }
        }
    }

    private void validarPaquetesEntregados(Envio envio, ArrayList<Paquete> paquetesEntregados) {
        Set<Paquete> sinRepetidos = new HashSet<>();
        List<Paquete> paquetesDelEnvio = envio.getPaquetes();
        for (Paquete paquete : paquetesEntregados) {
            if (paquete == null) {
                throw new IllegalArgumentException("La lista de paquetes entregados no puede contener null.");
            }
            if (!sinRepetidos.add(paquete)) {
                throw new IllegalArgumentException("La lista de paquetes entregados no puede tener repetidos.");
            }
            if (!paquetesDelEnvio.contains(paquete)) {
                throw new IllegalArgumentException("Hay paquetes entregados que no pertenecen al envío.");
            }
        }
    }

    private void validarClienteExistente(Cliente cliente) {
        if (cliente == null || !clientes.contains(cliente)) {
            throw new IllegalArgumentException("El cliente no existe en el sistema.");
        }
    }

    private void validarFuncionarioExistente(Funcionario funcionario) {
        if (funcionario == null || !funcionarios.contains(funcionario)) {
            throw new IllegalArgumentException("El funcionario no existe en el sistema.");
        }
    }

    private void validarPaqueteExistente(Paquete paquete) {
        if (paquete == null || !paquetes.contains(paquete)) {
            throw new IllegalArgumentException("El paquete no existe en el sistema.");
        }
    }

    private void validarEnvioExistente(Envio envio) {
        if (envio == null || !envios.contains(envio)) {
            throw new IllegalArgumentException("El envío no existe en el sistema.");
        }
    }

    private void validarTexto(String texto, String mensaje) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(mensaje);
        }
    }

    private void validarFecha(LocalDate fecha, String mensaje) {
        if (fecha == null) {
            throw new IllegalArgumentException(mensaje);
        }
    }

    private void validarZona(Zona zona) {
        if (zona == null) {
            throw new IllegalArgumentException("La zona no puede ser null.");
        }
    }

    private void validarEstadoPaquete(EstadoPaquete estado) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado del paquete no puede ser null.");
        }
    }

    private void validarNumeroFuncionario(int numeroFuncionario) {
        if (numeroFuncionario <= 0) {
            throw new IllegalArgumentException("El número de funcionario debe ser mayor a cero.");
        }
    }

    private void validarAnioIngreso(int anioIngreso) {
        if (anioIngreso <= 0) {
            throw new IllegalArgumentException("El año de ingreso debe ser mayor a cero.");
        }
        int anioActual = LocalDate.now().getYear();
        if (anioIngreso > anioActual) {
            throw new IllegalArgumentException("El año de ingreso no puede ser mayor al año actual.");
        }
    }

    private void validarPorcentaje(double porcentaje) {
        if (Double.isNaN(porcentaje) || Double.isInfinite(porcentaje)) {
            throw new IllegalArgumentException("El porcentaje ingresado no es válido.");
        }
    }
}
