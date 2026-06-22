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
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.Cliente;
import modelo.Paquete;
import modelo.SistemaEnvios;
import modelo.dtos.DatosPaquete;
import modelo.enums.Departamento;
import modelo.enums.TipoEvento;

/**
 *
 * @author Mauro Russo
 */
@SuppressWarnings("java:S1200")
public class VentanaIngresoPaquete extends JFrame implements PropertyChangeListener {

    private static final DateTimeFormatter FORMATO_FECHA
            = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final SistemaEnvios sistema;

    /**
     * Creates new form VentanaIngresoPaquete
     */
    public VentanaIngresoPaquete(SistemaEnvios sistema) {
        if (sistema == null) {
            throw new IllegalArgumentException("El sistema no puede ser null.");
        }
        this.sistema = sistema;
        initComponents();
        sistema.addPropertyChangeListener(this);
        configurarTabla();
        cargarClientes();
        cargarDepartamentos();
        cargarPaquetes();
        lblPrecio.setText("$ 0");
    }

    private void configurarTabla() {
        DefaultTableModel modeloTabla = new DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "Identificador",
                    "Cliente",
                    "Fecha",
                    "Destinatario",
                    "Dirección",
                    "Departamento",
                    "Zona",
                    "Peso (g)",
                    "Precio",
                    "Estado"
                }
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaPaquetes.setModel(modeloTabla);
    }

    private void cargarClientes() {
        DefaultComboBoxModel<Cliente> modeloCombo = new DefaultComboBoxModel<>();
        for (Cliente cliente : sistema.clientesOrdenadosPorNombre()) {
            modeloCombo.addElement(cliente);
        }
        cmbCliente.setModel(modeloCombo);
    }

    private void cargarDepartamentos() {
        DefaultComboBoxModel<Departamento> modeloCombo = new DefaultComboBoxModel<>();
        for (Departamento departamento : Departamento.values()) {
            modeloCombo.addElement(departamento);
        }
        cmbDepartamentoDestino.setModel(modeloCombo);
    }

    private void cargarPaquetes() {
        DefaultTableModel modeloTabla = (DefaultTableModel) tablaPaquetes.getModel();
        modeloTabla.setRowCount(0);
        for (Paquete paquete : sistema.getPaquetes()) {
            modeloTabla.addRow(new Object[]{
                paquete.getIdentificador(),
                paquete.getCliente().getNombre(),
                paquete.getFechaIngreso().format(FORMATO_FECHA),
                paquete.getDestinatario(),
                paquete.getDireccion(),
                paquete.getDepartamentoDestino(),
                paquete.getZona(),
                paquete.getPesoGramos(),
                paquete.getPrecio(),
                paquete.getEstado()
            });
        }
    }

    private void calcularPrecio() {
        try {
            Departamento departamento = obtenerDepartamentoSeleccionado();
            int pesoGramos = convertirPesoGramos();
            int precio = sistema.calcularPrecio(departamento, pesoGramos);
            lblPrecio.setText("$ " + precio);
        } catch (IllegalArgumentException | IllegalStateException e) {
            mostrarError(e.getMessage());
            lblPrecio.setText("$ 0");
        }
    }

    private void confirmarIngreso() {
        try {
            DatosPaquete datos = crearDatosIngresoPaquete();
            Paquete paquete = sistema.ingresarPaquete(datos);
            lblPrecio.setText("$ " + paquete.getPrecio());
            JOptionPane.showMessageDialog(
                    this,
                    "Paquete ingresado correctamente.",
                    "Ingreso de paquete",
                    JOptionPane.INFORMATION_MESSAGE
            );
            limpiarCampos();
        } catch (IllegalArgumentException | IllegalStateException e) {
            mostrarError(e.getMessage());
        }
    }

    private DatosPaquete crearDatosIngresoPaquete() {
        String identificador = txtIdentificador.getText().trim();
        Cliente cliente = obtenerClienteSeleccionado();
        LocalDate fecha = convertirFecha();
        String destinatario = txtDestinatario.getText().trim();
        String direccion = txtDireccion.getText().trim();
        Departamento departamentoDestino = obtenerDepartamentoSeleccionado();
        int pesoGramos = convertirPesoGramos();
        return new DatosPaquete(
                identificador,
                cliente,
                fecha,
                destinatario,
                direccion,
                departamentoDestino,
                pesoGramos
        );
    }

    private Cliente obtenerClienteSeleccionado() {
        Cliente cliente = (Cliente) cmbCliente.getSelectedItem();
        if (cliente == null) {
            throw new IllegalArgumentException("Debe seleccionar un cliente.");
        }
        return cliente;
    }

    private Departamento obtenerDepartamentoSeleccionado() {
        Departamento departamento = (Departamento) cmbDepartamentoDestino.getSelectedItem();
        if (departamento == null) {
            throw new IllegalArgumentException("Debe seleccionar un departamento destino.");
        }
        return departamento;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evento = evt.getPropertyName();
        if (TipoEvento.CLIENTES.name().equals(evento)
                || TipoEvento.PAQUETES.name().equals(evento)
                || TipoEvento.ENVIOS.name().equals(evento)
                || TipoEvento.RECEPCIONES.name().equals(evento)) {
            actualizarDatos();
        }
    }

    private LocalDate convertirFecha() {
        String texto = txtFecha.getText().trim();
        if (texto.isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar una fecha.");
        }
        try {
            return LocalDate.parse(texto, FORMATO_FECHA);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("La fecha debe tener formato dd/MM/yyyy.");
        }
    }

    private int convertirPesoGramos() {
        String texto = txtPesoGramos.getText().trim();
        if (texto.isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar el peso en gramos.");
        }
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El peso debe ser numérico.");
        }
    }

    private void limpiarCampos() {
        txtIdentificador.setText("");
        txtFecha.setText("");
        txtDestinatario.setText("");
        txtDireccion.setText("");
        txtPesoGramos.setText("");
        lblPrecio.setText("$ 0");
        if (cmbCliente.getItemCount() > 0) {
            cmbCliente.setSelectedIndex(0);
        }
        if (cmbDepartamentoDestino.getItemCount() > 0) {
            cmbDepartamentoDestino.setSelectedIndex(0);
        }
        txtIdentificador.requestFocus();
    }

    private void actualizarDatos() {
        Cliente clienteSeleccionado = (Cliente) cmbCliente.getSelectedItem();
        cargarClientes();
        cargarPaquetes();
        if (clienteSeleccionado != null) {
            seleccionarCliente(clienteSeleccionado);
        }
    }

    private void seleccionarCliente(Cliente cliente) {
        for (int i = 0; i < cmbCliente.getItemCount(); i++) {
            if (cmbCliente.getItemAt(i) == cliente) {
                cmbCliente.setSelectedIndex(i);
                return;
            }
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    @Override
    public void dispose() {
        sistema.removePropertyChangeListener(this);
        super.dispose();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblIdentificador = new javax.swing.JLabel();
        lblCliente = new javax.swing.JLabel();
        lblFecha = new javax.swing.JLabel();
        lblDestinatario = new javax.swing.JLabel();
        lblDireccion = new javax.swing.JLabel();
        lblDepartamentoDestino = new javax.swing.JLabel();
        lblPesoGramos = new javax.swing.JLabel();
        lblPrecioTitulo = new javax.swing.JLabel();
        txtIdentificador = new javax.swing.JTextField();
        txtFecha = new javax.swing.JTextField();
        txtDestinatario = new javax.swing.JTextField();
        txtDireccion = new javax.swing.JTextField();
        txtPesoGramos = new javax.swing.JTextField();
        cmbCliente = new javax.swing.JComboBox<>();
        cmbDepartamentoDestino = new javax.swing.JComboBox<>();
        lblPrecio = new javax.swing.JLabel();
        btnCerrar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        btnConfirmar = new javax.swing.JButton();
        btnCalcularPrecio = new javax.swing.JButton();
        scrollPaquetes = new javax.swing.JScrollPane();
        tablaPaquetes = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Ingreso de paquete");

        lblIdentificador.setText("Identificador:");

        lblCliente.setText("Cliente:");

        lblFecha.setText("Fecha:");

        lblDestinatario.setText("Destinatario:");

        lblDireccion.setText("Dirección:");

        lblDepartamentoDestino.setText("Departamento:");

        lblPesoGramos.setText("Peso en gramos:");

        lblPrecioTitulo.setText("Precio:");

        lblPrecio.setText("jLabel1");

        btnCerrar.setText("Cerrar");
        btnCerrar.addActionListener(this::btnCerrarActionPerformed);

        btnLimpiar.setText("Limpiar");
        btnLimpiar.addActionListener(this::btnLimpiarActionPerformed);

        btnConfirmar.setText("Confirmar");
        btnConfirmar.addActionListener(this::btnConfirmarActionPerformed);

        btnCalcularPrecio.setText("Calcular");
        btnCalcularPrecio.addActionListener(this::btnCalcularPrecioActionPerformed);

        tablaPaquetes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        scrollPaquetes.setViewportView(tablaPaquetes);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lblPesoGramos, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblDireccion, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblIdentificador, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblCliente, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblFecha, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblDestinatario, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblDepartamentoDestino, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cmbCliente, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtIdentificador)
                            .addComponent(txtFecha)
                            .addComponent(txtDestinatario)
                            .addComponent(txtDireccion)
                            .addComponent(txtPesoGramos)
                            .addComponent(cmbDepartamentoDestino, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(27, 27, 27)
                        .addComponent(scrollPaquetes, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(66, 326, Short.MAX_VALUE)
                        .addComponent(btnCalcularPrecio)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnConfirmar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnLimpiar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCerrar))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblPrecioTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scrollPaquetes, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblIdentificador)
                            .addComponent(txtIdentificador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblCliente)
                            .addComponent(cmbCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblFecha)
                            .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblDestinatario)
                            .addComponent(txtDestinatario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblDireccion)
                            .addComponent(txtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblDepartamentoDestino)
                            .addComponent(cmbDepartamentoDestino, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblPesoGramos)
                            .addComponent(txtPesoGramos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblPrecioTitulo)
                            .addComponent(lblPrecio))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnCerrar)
                            .addComponent(btnLimpiar)
                            .addComponent(btnConfirmar)
                            .addComponent(btnCalcularPrecio))
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCalcularPrecioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalcularPrecioActionPerformed
        calcularPrecio();
    }//GEN-LAST:event_btnCalcularPrecioActionPerformed

    private void btnConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmarActionPerformed
        confirmarIngreso();
    }//GEN-LAST:event_btnConfirmarActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        limpiarCampos();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCerrarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCalcularPrecio;
    private javax.swing.JButton btnCerrar;
    private javax.swing.JButton btnConfirmar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JComboBox<Cliente> cmbCliente;
    private javax.swing.JComboBox<Departamento> cmbDepartamentoDestino;
    private javax.swing.JLabel lblCliente;
    private javax.swing.JLabel lblDepartamentoDestino;
    private javax.swing.JLabel lblDestinatario;
    private javax.swing.JLabel lblDireccion;
    private javax.swing.JLabel lblFecha;
    private javax.swing.JLabel lblIdentificador;
    private javax.swing.JLabel lblPesoGramos;
    private javax.swing.JLabel lblPrecio;
    private javax.swing.JLabel lblPrecioTitulo;
    private javax.swing.JScrollPane scrollPaquetes;
    private javax.swing.JTable tablaPaquetes;
    private javax.swing.JTextField txtDestinatario;
    private javax.swing.JTextField txtDireccion;
    private javax.swing.JTextField txtFecha;
    private javax.swing.JTextField txtIdentificador;
    private javax.swing.JTextField txtPesoGramos;
    // End of variables declaration//GEN-END:variables

}
