/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package interfaces;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import modelo.Envio;
import modelo.Funcionario;
import modelo.Paquete;
import modelo.SistemaEnvios;
import modelo.enums.TipoEvento;
import modelo.enums.Zona;

/**
 *
 * @author Mauro Russo
 */
@SuppressWarnings("java:S1200")
public class VentanaEnvio extends JFrame implements PropertyChangeListener {

    private static final DateTimeFormatter FORMATO_FECHA
            = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final SistemaEnvios sistema;
    private final ArrayList<Paquete> paquetesSeleccionados;

    /**
     * Creates new form VentanaEnvio
     */
    public VentanaEnvio(SistemaEnvios sistema) {
        if (sistema == null) {
            throw new IllegalArgumentException("El sistema no puede ser null.");
        }
        this.sistema = sistema;
        this.paquetesSeleccionados = new ArrayList<>();
        initComponents();
        sistema.addPropertyChangeListener(this);
        cargarFuncionarios();
        cargarZonas();
        limpiarCampos();
    }

    private void cargarFuncionarios() {
        Funcionario seleccionado = (Funcionario) cmbFuncionario.getSelectedItem();
        DefaultComboBoxModel<Funcionario> modeloCombo = new DefaultComboBoxModel<>();
        for (Funcionario funcionario : sistema.funcionariosOrdenadosPorAnioIngresoDecreciente()) {
            modeloCombo.addElement(funcionario);
        }
        cmbFuncionario.setModel(modeloCombo);
        if (seleccionado != null) {
            seleccionarFuncionario(seleccionado);
        }
    }

    private void cargarZonas() {
        DefaultComboBoxModel<Zona> modeloCombo = new DefaultComboBoxModel<>();
        for (Zona zona : Zona.values()) {
            modeloCombo.addElement(zona);
        }
        cmbZona.setModel(modeloCombo);
    }

    private void cargarPaquetesPendientes() {
        DefaultListModel<Paquete> modeloLista = new DefaultListModel<>();
        Zona zona = obtenerZonaSeleccionadaSinValidar();
        if (zona != null) {
            for (Paquete paquete : sistema.paquetesPendientesDeZona(zona)) {
                if (!paquetesSeleccionados.contains(paquete)) {
                    modeloLista.addElement(paquete);
                }
            }
        }
        lstPaquetesPendientes.setModel(modeloLista);
    }

    private void cargarPaquetesSeleccionados() {
        DefaultListModel<Paquete> modeloLista = new DefaultListModel<>();
        for (Paquete paquete : paquetesSeleccionados) {
            modeloLista.addElement(paquete);
        }
        lstPaquetesSeleccionados.setModel(modeloLista);
    }

    private void agregarPaquetesSeleccionados() {
        for (Paquete paquete : lstPaquetesPendientes.getSelectedValuesList()) {
            if (!paquetesSeleccionados.contains(paquete)) {
                paquetesSeleccionados.add(paquete);
            }
        }
        actualizarListasYResumen();
    }

    private void quitarPaquetesSeleccionados() {
        for (Paquete paquete : lstPaquetesSeleccionados.getSelectedValuesList()) {
            paquetesSeleccionados.remove(paquete);
        }
        actualizarListasYResumen();
    }

    private void confirmarEnvio() {
        try {
            Funcionario funcionario = obtenerFuncionarioSeleccionado();
            LocalDate fechaEnvio = convertirFecha();
            Zona zona = obtenerZonaSeleccionada();
            if (paquetesSeleccionados.isEmpty()) {
                throw new IllegalArgumentException("Debe seleccionar al menos un paquete.");
            }
            Envio envio = sistema.ingresarEnvio(
                    funcionario,
                    fechaEnvio,
                    zona,
                    new ArrayList<>(paquetesSeleccionados)
            );
            JOptionPane.showMessageDialog(
                    this,
                    "Envío " + envio.getNumero() + " creado correctamente.",
                    "Envío confirmado",
                    JOptionPane.INFORMATION_MESSAGE
            );
            limpiarCampos();
        } catch (IllegalArgumentException | IllegalStateException e) {
            mostrarError(e.getMessage());
        }
    }

    private void limpiarCampos() {
        paquetesSeleccionados.clear();
        txtFechaEnvio.setText(LocalDate.now().format(FORMATO_FECHA));
        if (cmbFuncionario.getItemCount() > 0) {
            cmbFuncionario.setSelectedIndex(0);
        }
        if (cmbZona.getItemCount() > 0) {
            cmbZona.setSelectedIndex(0);
        }
        actualizarListasYResumen();
    }

    private void cambioDeZona() {
        paquetesSeleccionados.clear();
        actualizarListasYResumen();
    }

    private void actualizarListasYResumen() {
        removerPaquetesSeleccionadosInvalidos();
        cargarPaquetesPendientes();
        cargarPaquetesSeleccionados();
        actualizarResumen();
    }

    private void removerPaquetesSeleccionadosInvalidos() {
        Zona zona = obtenerZonaSeleccionadaSinValidar();
        paquetesSeleccionados.removeIf(paquete
                -> !paquete.estaPendiente()
                || zona == null
                || paquete.getZona() != zona
        );
    }

    private void actualizarResumen() {
        int cantidad = paquetesSeleccionados.size();
        int pesoTotalGramos = 0;
        int montoTotal = 0;
        for (Paquete paquete : paquetesSeleccionados) {
            pesoTotalGramos += paquete.getPesoGramos();
            montoTotal += paquete.getPrecio();
        }
        int kilos = pesoTotalGramos / 1000;
        int gramos = pesoTotalGramos % 1000;
        lblCantidad.setText(String.valueOf(cantidad));
        lblPesoTotal.setText(kilos + " kg " + gramos + " g");
        lblMontoTotal.setText("$ " + montoTotal);
    }

    private Funcionario obtenerFuncionarioSeleccionado() {
        Funcionario funcionario = (Funcionario) cmbFuncionario.getSelectedItem();
        if (funcionario == null) {
            throw new IllegalArgumentException("Debe seleccionar un funcionario.");
        }
        return funcionario;
    }

    private Zona obtenerZonaSeleccionada() {
        Zona zona = obtenerZonaSeleccionadaSinValidar();
        if (zona == null) {
            throw new IllegalArgumentException("Debe seleccionar una zona.");
        }
        return zona;
    }

    private Zona obtenerZonaSeleccionadaSinValidar() {
        return (Zona) cmbZona.getSelectedItem();
    }

    private LocalDate convertirFecha() {
        String texto = txtFechaEnvio.getText().trim();
        if (texto.isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar la fecha de envío.");
        }
        try {
            return LocalDate.parse(texto, FORMATO_FECHA);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("La fecha debe tener formato dd/MM/yyyy.");
        }
    }

    private void seleccionarFuncionario(Funcionario funcionario) {
        for (int i = 0; i < cmbFuncionario.getItemCount(); i++) {
            if (cmbFuncionario.getItemAt(i) == funcionario) {
                cmbFuncionario.setSelectedIndex(i);
                return;
            }
        }
    }

    private void actualizarDatos() {
        cargarFuncionarios();
        actualizarListasYResumen();
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblFuncionario = new javax.swing.JLabel();
        lblFechaEnvio = new javax.swing.JLabel();
        lblZona = new javax.swing.JLabel();
        cmbFuncionario = new javax.swing.JComboBox<>();
        txtFechaEnvio = new javax.swing.JTextField();
        cmbZona = new javax.swing.JComboBox<>();
        lblPaquetesPendientes = new javax.swing.JLabel();
        scrollPaquetesPendientes = new javax.swing.JScrollPane();
        lstPaquetesPendientes = new javax.swing.JList<>();
        lblPaquetesSeleccionados = new javax.swing.JLabel();
        scrollPaquetesSeleccionados = new javax.swing.JScrollPane();
        lstPaquetesSeleccionados = new javax.swing.JList<>();
        lblCantidadTitulo = new javax.swing.JLabel();
        lblPesoTotalTitulo = new javax.swing.JLabel();
        lblMontoTotalTitulo = new javax.swing.JLabel();
        lblCantidad = new javax.swing.JLabel();
        lblPesoTotal = new javax.swing.JLabel();
        lblMontoTotal = new javax.swing.JLabel();
        btnCerrar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        btnConfirmar = new javax.swing.JButton();
        btnQuitar = new javax.swing.JButton();
        btnAgregar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Envío de paquetes");

        lblFuncionario.setText("Funcionario:");

        lblFechaEnvio.setText("Fecha envío:");

        lblZona.setText("Zona:");

        cmbZona.addActionListener(this::cmbZonaActionPerformed);

        lblPaquetesPendientes.setText("Paquetes pendientes");

        scrollPaquetesPendientes.setViewportView(lstPaquetesPendientes);

        lblPaquetesSeleccionados.setText("Paquetes seleccionados");

        scrollPaquetesSeleccionados.setViewportView(lstPaquetesSeleccionados);

        lblCantidadTitulo.setText("Cantidad:");

        lblPesoTotalTitulo.setText("Peso total:");

        lblMontoTotalTitulo.setText("Monto:");

        lblCantidad.setText("0");

        lblPesoTotal.setText("0");

        lblMontoTotal.setText("0");

        btnCerrar.setText("Cerrar");
        btnCerrar.addActionListener(this::btnCerrarActionPerformed);

        btnLimpiar.setText("Limpiar");
        btnLimpiar.addActionListener(this::btnLimpiarActionPerformed);

        btnConfirmar.setText("Confirmar");
        btnConfirmar.addActionListener(this::btnConfirmarActionPerformed);

        btnQuitar.setText(">>");
        btnQuitar.addActionListener(this::btnQuitarActionPerformed);

        btnAgregar.setText("<<");
        btnAgregar.addActionListener(this::btnAgregarActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblFechaEnvio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblZona, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblFuncionario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(cmbFuncionario, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtFechaEnvio))
                            .addComponent(cmbZona, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(lblPaquetesPendientes)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(scrollPaquetesPendientes, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnAgregar, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnQuitar, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lblMontoTotalTitulo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblCantidadTitulo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblPesoTotalTitulo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblMontoTotal, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
                            .addComponent(lblCantidad, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblPesoTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollPaquetesSeleccionados, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPaquetesSeleccionados))
                .addContainerGap(17, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnConfirmar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLimpiar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCerrar)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblFuncionario)
                            .addComponent(cmbFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblFechaEnvio)
                            .addComponent(txtFechaEnvio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblZona)
                            .addComponent(cmbZona, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(40, 40, 40)
                        .addComponent(lblPaquetesPendientes)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPaquetesPendientes, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addComponent(btnAgregar)
                                .addGap(18, 18, 18)
                                .addComponent(btnQuitar))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblPaquetesSeleccionados)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scrollPaquetesSeleccionados, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(45, 45, 45)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCantidadTitulo)
                    .addComponent(lblCantidad))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPesoTotalTitulo)
                    .addComponent(lblPesoTotal))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMontoTotalTitulo)
                    .addComponent(lblMontoTotal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCerrar)
                    .addComponent(btnLimpiar)
                    .addComponent(btnConfirmar))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmarActionPerformed
        confirmarEnvio();
    }//GEN-LAST:event_btnConfirmarActionPerformed

    private void btnQuitarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuitarActionPerformed
        quitarPaquetesSeleccionados();
    }//GEN-LAST:event_btnQuitarActionPerformed

    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed
        agregarPaquetesSeleccionados();
    }//GEN-LAST:event_btnAgregarActionPerformed

    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCerrarActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        limpiarCampos();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void cmbZonaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbZonaActionPerformed
        cambioDeZona();
    }//GEN-LAST:event_cmbZonaActionPerformed

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evento = evt.getPropertyName();
        if (TipoEvento.FUNCIONARIOS.name().equals(evento)
                || TipoEvento.PAQUETES.name().equals(evento)
                || TipoEvento.ENVIOS.name().equals(evento)
                || TipoEvento.RECEPCIONES.name().equals(evento)) {
            actualizarDatos();
        }
    }

    @Override
    public void dispose() {
        sistema.removePropertyChangeListener(this);
        super.dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregar;
    private javax.swing.JButton btnCerrar;
    private javax.swing.JButton btnConfirmar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnQuitar;
    private javax.swing.JComboBox<Funcionario> cmbFuncionario;
    private javax.swing.JComboBox<Zona> cmbZona;
    private javax.swing.JLabel lblCantidad;
    private javax.swing.JLabel lblCantidadTitulo;
    private javax.swing.JLabel lblFechaEnvio;
    private javax.swing.JLabel lblFuncionario;
    private javax.swing.JLabel lblMontoTotal;
    private javax.swing.JLabel lblMontoTotalTitulo;
    private javax.swing.JLabel lblPaquetesPendientes;
    private javax.swing.JLabel lblPaquetesSeleccionados;
    private javax.swing.JLabel lblPesoTotal;
    private javax.swing.JLabel lblPesoTotalTitulo;
    private javax.swing.JLabel lblZona;
    private javax.swing.JList<Paquete> lstPaquetesPendientes;
    private javax.swing.JList<Paquete> lstPaquetesSeleccionados;
    private javax.swing.JScrollPane scrollPaquetesPendientes;
    private javax.swing.JScrollPane scrollPaquetesSeleccionados;
    private javax.swing.JTextField txtFechaEnvio;
    // End of variables declaration//GEN-END:variables
}
